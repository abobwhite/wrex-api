package com.daugherty.wrex.changelog

import com.github.cloudyrock.mongock.ChangeLog
import com.github.cloudyrock.mongock.ChangeSet
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document

@ChangeLog(order = '002')
class Changelog_002_LinesOfService {
  @ChangeSet(order = '001', id = 'linesOfService', author = 'ARW0214')
  static void addLinesOfService(MongoDatabase db) {
    MongoCollection<Document> branchesCollection = db.getCollection("linesOfService")

    branchesCollection.insertMany([
      new Document([name: "Product Engineering & Innovation", abbrev: "PE&I"]),
      new Document([name: "Product Leadership", abbrev: "PL"]),
      new Document([name: "Software Architecture & Engineering", abbrev: "SA&E"]),
      new Document([name: "Data & Analytics", abbrev: "D&A"]),
      new Document([name: "Delivery Leadership", abbrev: "DL"]),
      new Document([name: "Business Alignment", abbrev: "BA"]),
      new Document([name: "Management Consulting", abbrev: "MC"]),
    ])
  }
}
