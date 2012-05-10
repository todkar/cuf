package repositories

import org.neo4j.kernel.EmbeddedGraphDatabase
import org.neo4j.graphdb._
import collection.JavaConversions._

import models.CreditUnion
import models.User

class CreditUnionRepository(val neo: GraphDatabaseService) {
  //  val neo: GraphDatabaseService = new EmbeddedGraphDatabase("var/graphdb")

  def create(creditUnion: CreditUnion): Unit = {
    var tx: Transaction = neo.beginTx
    val creditUnionReferenceNode = neo.createNode

    neo.getReferenceNode.createRelationshipTo(creditUnionReferenceNode, "creditUnionReference")

    var creditUnionNode: Node = null
    var locationNode: Node = null
    val nodeIndex = neo.index.forNodes("nodes")
    try {
      creditUnionNode = neo.createNode
      creditUnionNode.setProperty("name", creditUnion.name)
      nodeIndex.add(creditUnionNode, "creditUnion", creditUnion.name)
      creditUnionReferenceNode.createRelationshipTo(creditUnionNode, "isA")

      locationNode = neo.createNode
      locationNode.setProperty("name", creditUnion.acceptsWorkingIn)
      nodeIndex.add(locationNode, "location", creditUnion.acceptsWorkingIn)

      creditUnionNode.createRelationshipTo(locationNode, "acceptsWorkingIn")

      tx.success
    } finally {
      tx.finish
    }
  }

  def createUser(user: User): Unit = {
    val tx: Transaction = neo.beginTx()
    val nodeIndex = neo.index.forNodes("nodes")
    val userReferenceNode = neo.createNode

    try {
      neo.getReferenceNode.createRelationshipTo(userReferenceNode, "userReference")
      nodeIndex.add(userReferenceNode, "userReference", "userReference")

      val userNode = neo.createNode
      userNode.setProperty("name", user.name)
      userNode.setProperty("worksIn", user.worksIn)

      nodeIndex.add(userNode, "user", user.name)

      userReferenceNode.createRelationshipTo(userNode, "isAUser")

      //    	val locationNode = neo.index.forNodes("nodes").get("location", user.worksIn).getSingle
      //    	userNode.createRelationshipTo(locationNode, "worksIn")
      tx.success
    } finally {
      tx.finish
    }
  }

  //  def find(user: User): List[CreditUnion] = {
  //    neo.getReferenceNode()
  //  }
  //try {
  //
  //      val trav: Traverser = first.traverse(x`x`)
  //
  //      for (node <- trav) {
  //        println(node.getProperty("name"))
  //      }
  //      tx.success()
  //    } finally {
  //      tx.finish()
  //      println("finished transaction 2")
  //    }

  implicit def stringToRelationshipType(x: String): RelationshipType = DynamicRelationshipType.withName(x)
}