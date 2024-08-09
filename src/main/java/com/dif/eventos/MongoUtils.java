package com.dif.eventos;
import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.net.UnknownHostException;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
public class MongoUtils {
    public static String CONNECTION_STRING = "mongodb://localhost:27017/";
    public static String DB_NAME = "DIF";
    public static MongoDatabase db;
    public static MongoDatabase connect() throws UnknownHostException{
        String uri = CONNECTION_STRING;
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .serverApi(serverApi)
                .build();

        MongoClient mongoClient = MongoClients.create(settings);
        db = mongoClient.getDatabase(DB_NAME);
        Bson command = new BsonDocument("ping", new BsonInt64(1));
        Document commandResult = db.runCommand(command);
        System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
        return db;
    }
    public static String findOneDocument(MongoDatabase database, String collection, String key, String value, String[] qry) {
        try{
            MongoCollection<Document> col = database.getCollection(collection);
            Document findDocument = new Document(key, value);

            FindIterable<Document> resultDocument;
            if(qry == null){
                resultDocument = col.find(findDocument);
            } else {
                resultDocument = col.find(findDocument).projection(fields(include(qry)));
            }
            return resultDocument.first().toJson();
        } catch (Exception ex){
            System.err.println(ex.getMessage());
            return null;
        }
    }
    public static FindIterable<Document> findAllDocuments(String collection, String key, Object value, String[] qry) {
        try{
            MongoDatabase db = MongoUtils.connect();
            MongoCollection<Document> col = db.getCollection(collection);
            Document findDocument = new Document();
            FindIterable<Document> resultDocument;

            if(key == null && qry == null){
                resultDocument = col.find();
            } else if(key != null && qry == null){
                findDocument.put(key,value);
                resultDocument = col.find(findDocument);
            } else if(key != null && qry != null){
                findDocument.put(key,value);
                resultDocument = col.find(findDocument).projection(fields(include(qry)));
            } else {
                resultDocument = col.find().projection(fields(include(qry)));
            }
            System.out.println("resultDocument: " + resultDocument);
            return resultDocument;

        } catch (Exception ex){
            System.err.println(ex.getMessage());
            return null;
        }
    }
    public static Boolean deleteOne(String collection, String key, String value) throws UnknownHostException {
        MongoDatabase db = connect();
        MongoCollection<Document> documents = db.getCollection(collection);
        Bson query = eq(key, value);
        try {
            DeleteResult result;
            if(key.equals("_id")){
                result = documents.deleteOne(new Document(key, new ObjectId(value)));
            } else {
                result = documents.deleteOne(query);
            }
            return result.getDeletedCount() == 1;
        } catch (MongoException me) {
            System.err.println("Unable to delete due to an error: " + me);
            return false;
        }
    }
    public static String insertDocumentInDB(String collection, Document document) throws UnknownHostException {
        MongoDatabase db = connect();
        MongoCollection<Document> col = db.getCollection(collection);
            try {
                InsertOneResult result = col.insertOne(document);
                String idResult = result.getInsertedId().toString().substring(result.getInsertedId().toString().indexOf("=") + 1, result.getInsertedId().toString().indexOf("}"));
                return idResult;
            } catch (MongoException me) {
                System.err.println("Unable to insert due to an error: " + me.getMessage());
                return null;
            }
    }
    public static Boolean updateDocumentInDB(String collection, Bson filter, Bson updates) throws UnknownHostException{
        MongoDatabase db = connect();
        MongoCollection<Document> col = db.getCollection(collection);
        try {
            UpdateResult result = col.updateOne(filter, updates);
            System.out.println("col: " + col);
            System.out.println("Documento actualizado con éxito.");
            return result.getModifiedCount() == 1;
        } catch (MongoException me){
            System.err.println("Exception: " + me.getMessage());
            return false;
        }
    }
}
