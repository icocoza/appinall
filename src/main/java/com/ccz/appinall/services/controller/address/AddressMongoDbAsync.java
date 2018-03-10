package com.ccz.appinall.services.controller.address;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bson.Document;

import com.mongodb.MongoCompressor;
import com.mongodb.MongoCredential;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.connection.ClusterSettings;

public class AddressMongoDbAsync {
	MongoClient mongoClient;
	MongoDatabase mongoDatabase;
	Map<String, MongoCollection<Document>> collectionMap = new ConcurrentHashMap<>();
	
	public AddressMongoDbAsync(String[] urls, int port, String dbName) {
		ClusterSettings clusterSettings = ClusterSettings.builder()
				.hosts(Arrays.stream(urls).map(url -> new ServerAddress(url, port)).collect(Collectors.toList())).build();
		MongoClientSettings settings = MongoClientSettings.builder().clusterSettings(clusterSettings)
				.compressorList(Arrays.asList(MongoCompressor.createSnappyCompressor()))
				.build();
		//.streamFactoryFactory(NettyStreamFactoryFactory.builder().build()).build());	//MongoClientSettings with the StreamFactory set to use Netty:
		mongoClient = MongoClients.create(settings);
		mongoDatabase = mongoClient.getDatabase(dbName);
	}
	
	public AddressMongoDbAsync(String[] urls, int port, String dbName, String userName, String password) {
		MongoCredential credential = MongoCredential.createCredential(userName, dbName, password.toCharArray());
		ClusterSettings clusterSettings = ClusterSettings.builder()
				.hosts(Arrays.stream(urls).map(url -> new ServerAddress(url, port)).collect(Collectors.toList())).build();
		MongoClientSettings settings = MongoClientSettings.builder()
                .clusterSettings(clusterSettings)
                .credential(credential)
                .compressorList(Arrays.asList(MongoCompressor.createSnappyCompressor()))
                .build();
		mongoClient = MongoClients.create(settings);
		mongoDatabase = mongoClient.getDatabase(dbName);
	}
	
	private MongoCollection<Document> getCollection(String collectionName) {
		MongoCollection<Document> collection = collectionMap.get(collectionName);
		if(collection == null) {
			collection = mongoDatabase.getCollection(collectionName)
					.withReadPreference(ReadPreference.primary())
	                .withReadConcern(ReadConcern.MAJORITY)
	                .withWriteConcern(WriteConcern.MAJORITY);;
			collectionMap.put(collectionName, collection);
		}
		return collection;
	}
	
	public void insert(String collectionName, Document doc) {
		getCollection(collectionName).insertOne(doc, new SingleResultCallback<Void>() {
		    @Override
		    public void onResult(final Void result, final Throwable t) {
		    }
		});
	}
	public void insert(String collectionName, String json) {
		this.insert(collectionName, Document.parse(json));
	}
	
	public void bulkInsert(String collectionName, List<Document> docs) {
		List<InsertOneModel<Document>> bulkList = docs.stream().map(item -> new InsertOneModel<>(item)).collect(Collectors.toList());
		this.getCollection(collectionName).bulkWrite(bulkList, new SingleResultCallback<BulkWriteResult>() {
		    @Override
		    public void onResult(final BulkWriteResult result, final Throwable t) {
		    }
		});
	}
	
	public void bulkInsertJson(String collectionName, List<String> jsonList) {
		List<Document> docList = jsonList.stream().map(json -> Document.parse(json)).collect(Collectors.toList());
		this.bulkInsert(collectionName, docList);
	}
}
