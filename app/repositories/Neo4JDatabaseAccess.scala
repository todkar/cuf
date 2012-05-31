package repositories

import org.neo4j.kernel.EmbeddedGraphDatabase
import org.neo4j.graphdb._
import models.CreditUnion
import models.User

class Neo4JDatabaseAccess(val neo: GraphDatabaseService) {
  var creditUnionReferenceNode: Node = null
  var userReferenceNode: Node = null
  val nodeIndex = neo.index.forNodes("nodes")
  withTransaction {
	creditUnionReferenceNode = neo.createNode
	userReferenceNode = neo.createNode
    neo.getReferenceNode.createRelationshipTo(creditUnionReferenceNode, "creditUnionReference")
    neo.getReferenceNode.createRelationshipTo(userReferenceNode, "userReference")
    nodeIndex.add(userReferenceNode, "userReference", "userReference")
  }

  def createCreditUnion(creditUnion: CreditUnion): Node = {
    var creditUnionNode: Node = null
    withTransaction {
      creditUnionNode = neo.createNode
      creditUnionNode.setProperty("name", creditUnion.name)
    }
    creditUnionNode
  }

  def attachToCreditUnionReferenceNode(creditUnionNode: Node): Unit = {
    withTransaction {
      creditUnionReferenceNode.createRelationshipTo(creditUnionNode, "isA")
    }
  }

  def index(node: Node, nodeType: String, nodeKey: String): Unit = {
    withTransaction {
      nodeIndex.add(node, nodeType, nodeKey)
    }
  }

  def createLocationNode(location: String): Node = {
    var locationNode: Node = null
    withTransaction {
      val locationNodes = neo.index.forNodes("nodes").get("location", location)
      locationNode = if (locationNodes.hasNext()) {
        locationNodes.next()
      } else {
        val node = neo.createNode()
        node.setProperty("name", location)
        neo.index.forNodes("nodes").add(node, "location", location)
        node
      }
    }
    locationNode
  }

  def createRelationship(source: Node, destination: Node, relationshipType: String): Unit = {
    withTransaction {
      source.createRelationshipTo(destination, relationshipType)
    }
  }

  def create(user: User): Node = {
    var userNode: Node = null
    withTransaction {
      userNode = neo.createNode
      userNode.setProperty("name", user.name)
    }
    userNode
  }

  def attachToUserReferenceNode(node: Node): Unit = {
    withTransaction {
      userReferenceNode.createRelationshipTo(node, "isAUser")
    }
  }

  private def withTransaction(func: => Unit) {
    var tx: Transaction = neo.beginTx
    try {
      func
      tx.success
    } finally {
      tx.finish
    }
  }

  implicit def stringToRelationshipType(x: String): RelationshipType = DynamicRelationshipType.withName(x)

}