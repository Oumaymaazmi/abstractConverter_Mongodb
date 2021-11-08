/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import bean.Car;
import com.example.projectNamebfvdcsx.infrastructure.service.util.DateUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.sun.faces.facelets.tag.jstl.core.ForEachHandler;
import converter.CarConverter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bson.types.ObjectId;

/**
 *
 * @author a
 */
public abstract class AbstaractMongodb<T> {

    private Class<T> pojo;
    private DBCollection col;
    MongoClient mongoClient = new MongoClient();
    private ConfigCollection configCollection = new ConfigCollection();

    public AbstaractMongodb(Class<T> pojo) {
        this.pojo = pojo;
        Field[] fields = configCollection.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (pojo.getSimpleName().toLowerCase().equals(field.getName())) {
                this.col = mongoClient.getDB("test").getCollection(field.getName());
            }
        }
    }

    public DBObject toObject(T t) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = t.getClass().getDeclaredFields();
        BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName() == "_id" && field.get(t) != null) {
                builder.append("_id", new ObjectId(field.get(t).toString()));
            }

            if (field.get(t) != null) {
                builder.append(field.getName(), field.get(t).toString());
            }
        }
        return builder.get();

    }

    public T toDocument(DBObject object, T t) throws IllegalArgumentException, IllegalAccessException {
        Set<String> keys = object.keySet();
        Iterator iterator = keys.iterator();
        Field[] fields = t.getClass().getDeclaredFields();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getType().toString().contains("String")) {
                    field.set(t, object.get(key).toString());
                     System.out.println("String llllllllllllll");
                }
                if (field.getType().toString().contains("Date")) {
                    field.set(t, DateUtil.parse(object.get(key).toString()));
                }
                if ("int" == (field.getType().toString())) {
                    System.out.println("intlllllllllllllllllllllll ");
                    field.setInt(t, Integer.parseInt(object.get(key).toString()));
                }
                if ("double"==(field.getType().toString())) {
                    field.setDouble(t, Double.parseDouble(object.get(key).toString()));
                }
                if ("long"==(field.getType().toString())) {
                    field.setLong(t, Long.parseLong(object.get(key).toString()));
                }
                if ("float"==(field.getType().toString())) {
                    field.setFloat(t, Float.parseFloat(object.get(key).toString()));
                }
                if ("boolean"==(field.getType().toString())) {
                    field.setBoolean(t, Boolean.parseBoolean(object.get(key).toString()));
                }
            }
            return t;
        }
        return null;
    }

    public Class<T> getPojo() {
        return pojo;
    }

    public void setPojo(Class<T> pojo) {
        this.pojo = pojo;
    }

    public DBCollection getCol() {
        return col;
    }

    public void setCol(DBCollection col) {
        this.col = col;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public ConfigCollection getConfigCollection() {
        return configCollection;
    }

    public void setConfigCollection(ConfigCollection configCollection) {
        this.configCollection = configCollection;
    }

}
