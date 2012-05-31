package repositories

import org.neo4j.graphdb.GraphDatabaseService

import models.CreditUnion
import models.User

class CreditUnionRepository(neo: GraphDatabaseService) extends Neo4JDatabaseAccess(neo) {

  def create(creditUnion: CreditUnion): Unit = {
    val creditUnionNode = createCreditUnion(creditUnion)
    index(creditUnionNode, "creditUnion", creditUnion.name)

    val locationNode = createLocationNode(creditUnion.acceptsWorkingIn)

    createRelationship(creditUnionNode, locationNode, "acceptsWorkingIn")
  }

  def createUser(user: User): Unit = {
    val userNode = create(user)
    index(userNode, "user", user.name)
    createRelationship(userReferenceNode, userNode, "isAUser")
    val locationNode = createLocationNode(user.worksIn)

    createRelationship(userNode, locationNode, "worksIn")
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
}