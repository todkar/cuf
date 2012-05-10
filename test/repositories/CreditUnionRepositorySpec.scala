package repositories

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.Assertions
import org.neo4j.test.ImpermanentGraphDatabase
import org.neo4j.test.TestGraphDatabaseFactory
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb._
import collection.JavaConversions._
import models.CreditUnion
import models.User
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class CreditUnionRepositorySpec extends FunSuite with BeforeAndAfter with ShouldMatchers {

  var graphDb: GraphDatabaseService = _
  
  before {
    graphDb = new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder().newGraphDatabase()
  }
  
  after {
    graphDb.shutdown()
  }

  test("should add credit union node to graph nodes index") {
    val repository = new CreditUnionRepository(graphDb)
    val creditUnion = new CreditUnion("Manhattan Credit Union", "Manhattan")
    repository.create(creditUnion)

    val creditUnionNode = graphDb.index.forNodes("nodes").get("creditUnion", creditUnion.name).getSingle()
    assert(creditUnionNode.getProperty("name") === "Manhattan Credit Union")
  }

  test("should add location node to graph nodes index") {
    val repository = new CreditUnionRepository(graphDb)
    val creditUnion = new CreditUnion("Manhattan Credit Union", "Manhattan")
    repository.create(creditUnion)

    val locationNode = graphDb.index.forNodes("nodes").get("location", creditUnion.acceptsWorkingIn).getSingle()
    assert(locationNode.getProperty("name") === "Manhattan")
  }

  test("should build credit union node, location node and a relationship between them") {
    val repository = new CreditUnionRepository(graphDb)
    val creditUnion = new CreditUnion("Manhattan Credit Union", "Manhattan")
    repository.create(creditUnion)

    val creditUnionNode = graphDb.index.forNodes("nodes").get("creditUnion", creditUnion.name).getSingle()

    val locationNodes = creditUnionNode.traverse(Traverser.Order.BREADTH_FIRST,
    							StopEvaluator.END_OF_GRAPH,
    							ReturnableEvaluator.ALL_BUT_START_NODE,
    							"acceptsWorkingIn" : RelationshipType,
    							Direction.OUTGOING).toList

    assert(locationNodes(0).getProperty("name") === "Manhattan")
  }

  test("should add the user to the graph nodes index") {
    val repository = new CreditUnionRepository(graphDb)
    val user = new User("Praful", "Manhattan")
    
    repository.createUser(user)
    
    val userNode = graphDb.index.forNodes("nodes").get("user", user.name).getSingle()
    assert(userNode.getProperty("name") === "Praful")
  }
  
  test("should add a user to the user reference node") {
    val repository = new CreditUnionRepository(graphDb)
    val user = new User("Praful", "Manhattan")
    
    repository.createUser(user)

    val userReferenceNode = graphDb.index.forNodes("nodes").get("userReference", "userReference").getSingle()
    val allUsers = userReferenceNode.traverse(Traverser.Order.BREADTH_FIRST,
    							StopEvaluator.END_OF_GRAPH,
    							ReturnableEvaluator.ALL_BUT_START_NODE,
    							"isAUser" : RelationshipType,
    							Direction.OUTGOING).toList
    allUsers.size should equal (1)
    val foundUserNode = allUsers.get(0)
    foundUserNode.getProperty("name") should be === (user.name)
    foundUserNode.getProperty("worksIn") should be === (user.worksIn)
  }
  
  test("should build a user node, location node and a relationship between them") {
    
  }

  
  implicit def stringToRelationshipType(x: String): RelationshipType = DynamicRelationshipType.withName(x)

}