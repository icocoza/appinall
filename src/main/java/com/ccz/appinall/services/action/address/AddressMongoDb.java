package com.ccz.appinall.services.action.address;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;

public class AddressMongoDb {
	
	MongoClient mongoClient;	//embeded pool
	MongoDatabase mongoDatabase;
	Map<String, MongoCollection<Document>> collectionMap = new ConcurrentHashMap<>();
	String collectionName;
	
	public AddressMongoDb(String url, int port, String dbName, String collectionName) {
		this.collectionName = collectionName;
		mongoClient = new MongoClient(url, port);
		mongoDatabase = mongoClient.getDatabase(dbName);
		MongoIterable<String> mits = mongoDatabase.listCollectionNames();
		for(String it : mits)
			if(it.equals(collectionName))
				return;
		mongoDatabase.createCollection(collectionName, new CreateCollectionOptions().autoIndex(false));
		 
	}
	
	public void createUpsertIndex() {
		IndexOptions indexOptions = new IndexOptions().unique(true);//.defaultLanguage("kr");
		this.getCollection().createIndex(Indexes.ascending("buildmgr"), indexOptions);
	}
	
	public void createSearchIndex() {
		ListIndexesIterable<Document> idxList = this.getCollection().listIndexes();
		for(Document doc : idxList)
			if(doc.getString("name").equals("SearchIndex"))
				return;
		
		this.getCollection().createIndex(Indexes.compoundIndex(Indexes.text("zip"), Indexes.text("sido"), Indexes.text("sigu"), Indexes.text("eub")
				, Indexes.text("rname"), Indexes.text("delivery"), Indexes.text("dongname"), Indexes.text("liname"), Indexes.text("hjdongname")), new IndexOptions().name("SearchIndex"));
	}
	
	private MongoCollection<Document> getCollection() {
		MongoCollection<Document> collection = collectionMap.get(collectionName);
		if(collection == null) {
			collection = mongoDatabase.getCollection(collectionName);
			collectionMap.put(collectionName, collection);
		}
		return collection;
	}
	
	public void insert(Document doc) {
		try {
			this.getCollection().insertOne(doc);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void insert(String json) {
		this.insert(Document.parse(json));
	}

	public void upsertMany(List<Document> docs) {
		UpdateOptions updateOptions = new UpdateOptions().upsert(true);
		List<UpdateOneModel<Document>> bulkList = docs.stream().map(item -> new UpdateOneModel<Document>(new Document("buildmgr", item.get("buildmgr")), item, updateOptions)).collect(Collectors.toList());
		this.getCollection().bulkWrite(bulkList, new BulkWriteOptions().ordered(false));
	}
	
	public void upsertManyJson(List<String> jsonList) {
		List<Document> docList = jsonList.stream().map(json -> Document.parse(json)).collect(Collectors.toList());
		this.bulkInsert(docList);
	}

	public void bulkInsert(List<Document> docs) {
		List<InsertOneModel<Document>> bulkList = docs.stream().map(item -> new InsertOneModel<>(item)).collect(Collectors.toList());
		this.getCollection().bulkWrite(bulkList, new BulkWriteOptions().ordered(false));
	}
	
	public void bulkInsertJson(List<String> jsonList) {
		List<Document> docList = jsonList.stream().map(json -> Document.parse(json)).collect(Collectors.toList());
		this.bulkInsert(docList);
	}

	public void searchAddr(String words) {
		//this.getCollection(collectionName).bulkWrite(requests)
	}
	
	
}
