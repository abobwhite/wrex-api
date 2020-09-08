package com.daugherty.wrex.changelog

import com.github.cloudyrock.mongock.ChangeLog
import com.github.cloudyrock.mongock.ChangeSet
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document

@ChangeLog(order = '003')
class Changelog_003_DefaultCategories {
  @ChangeSet(order = '001', id = 'defaultCategories', author = 'ARW0214')
  static void addDefaultCategories(MongoDatabase db) {
    MongoCollection<Document> branchesCollection = db.getCollection('tagCategories')

    branchesCollection.insertMany([
      new Document([name: 'Application Framework']),
      new Document([name: 'Agile Tool']),
      new Document([name: 'Programming Language']),
      new Document([name: 'Event']),
      new Document([name: 'Certification']),
      new Document([name: 'Hobby']),
      new Document([name: 'IDE'])
    ])
  }
}
