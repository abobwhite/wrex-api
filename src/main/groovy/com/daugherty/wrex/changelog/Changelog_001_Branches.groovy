package com.daugherty.wrex.changelog

import com.github.cloudyrock.mongock.ChangeLog
import com.github.cloudyrock.mongock.ChangeSet
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document

@ChangeLog(order = '001')
class Changelog_001_Branches {
  @ChangeSet(order = '001', id = 'branches', author = 'ARW0214')
  static void addBranches(MongoDatabase db) {
    MongoCollection<Document> branchesCollection = db.getCollection("branches")

    branchesCollection.insertMany([
      new Document([name: "St. Louis", abbrev: "STL"]),
      new Document([name: "Atlanta", abbrev: "ATL"]),
      new Document([name: "Chicago", abbrev: "CHI"]),
      new Document([name: "Dallas", abbrev: "DAL"]),
      new Document([name: "Minneapolis", abbrev: "MSP"]),
      new Document([name: "New York", abbrev: "NYA"])
    ])
  }
}
