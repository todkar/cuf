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

      val locationNode = getLocationFromGraph(creditUnion.acceptsWorkingIn)

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
      nodeIndex.add(userNode, "user", user.name)

      userReferenceNode.createRelationshipTo(userNode, "isAUser")
      val locationNode = getLocationFromGraph(user.worksIn)

      userNode.createRelationshipTo(locationNode, "worksIn")

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

  def getReturnEvaluator: ReturnableEvaluator = {
    val returnEvaluator: ReturnableEvaluator = new ReturnableEvaluator() {
      def isReturnableNode(position: TraversalPosition): Boolean =
        {
          // Return nodes that don't have any outgoing relationships,
          // only incoming relationships, i.e. leaf nodes.
          return !position.currentNode().hasRelationship(
            Direction.OUTGOING);
        }
    }
    returnEvaluator
  }

  implicit def stringToRelationshipType(x: String): RelationshipType = DynamicRelationshipType.withName(x)

  private def getLocationFromGraph(location: String): Node = {
    val locationNodes = neo.index.forNodes("nodes").get("location", location)
    val locationNode = if (locationNodes.hasNext()) {
      locationNodes.next()
    } else {
      val node = neo.createNode()
      node.setProperty("name", location)
      neo.index.forNodes("nodes").add(node, "location", location)
      node
    }
    locationNode
  }
}