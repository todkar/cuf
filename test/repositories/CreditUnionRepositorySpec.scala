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

  test("credit union should attach to existing location node if it already exists") {
    val repository = new CreditUnionRepository(graphDb)
    val manhattanCreditUnion = new CreditUnion("Manhattan Credit Union", "Manhattan")
    repository.create(manhattanCreditUnion)

    val brooklynCreditUnion = new CreditUnion("Brooklyn Credit Union", "Manhattan")

    repository.create(brooklynCreditUnion)

    val locations = graphDb.index.forNodes("nodes").get("location", manhattanCreditUnion.acceptsWorkingIn)

    locations.size should equal(1)
  }

  test("should build credit union node, location node and a relationship between them") {
    val repository = new CreditUnionRepository(graphDb)
    val creditUnion = new CreditUnion("Manhattan Credit Union", "Manhattan")
    repository.create(creditUnion)

    val creditUnionNode = graphDb.index.forNodes("nodes").get("creditUnion", creditUnion.name).getSingle()

    val locationNodes = creditUnionNode.traverse(Traverser.Order.BREADTH_FIRST,
      StopEvaluator.END_OF_GRAPH,
      ReturnableEvaluator.ALL_BUT_START_NODE,
      "acceptsWorkingIn": RelationshipType,
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
      "isAUser": RelationshipType,
      Direction.OUTGOING).toList
    allUsers.size should equal(1)
    val foundUserNode = allUsers.get(0)
    foundUserNode.getProperty("name") should be === (user.name)
  }

  test("should build a user node, location node and a relationship between them") {
    val repository = new CreditUnionRepository(graphDb)
    val user = new User("Praful", "Manhattan")

    repository.createUser(user)

    val userNode = graphDb.index.forNodes("nodes").get("user", user.name).getSingle()
    val allLocations = userNode.traverse(Traverser.Order.BREADTH_FIRST,
      StopEvaluator.END_OF_GRAPH,
      ReturnableEvaluator.ALL_BUT_START_NODE,
      "worksIn": RelationshipType,
      Direction.OUTGOING).toList
    allLocations.size should equal(1)
    val foundUserNode = allLocations.get(0)
    foundUserNode.getProperty("name") should be === (user.worksIn)
  }

  test("should add the user's location to the graph nodes index") {
    val repository = new CreditUnionRepository(graphDb)
    val user = new User("Praful", "Manhattan")

    repository.createUser(user)

    val locationNode = graphDb.index.forNodes("nodes").get("location", user.worksIn).getSingle()
    assert(locationNode.getProperty("name") === "Manhattan")
  }

  test("should attach to existing location node if it already exists") {
    val repository = new CreditUnionRepository(graphDb)
    val praful = new User("Praful", "Manhattan")

    repository.createUser(praful)

    val max = new User("Max", "Manhattan")

    repository.createUser(max)

    val locations = graphDb.index.forNodes("nodes").get("location", praful.worksIn)

    locations.size should equal(1)
  }

  test("should add user and a credit union to graph and should be able to navigate from user to credit union based on working location") {
    val repository = new CreditUnionRepository(graphDb)
    val creditUnion = new CreditUnion("Manhattan Credit Union", "Manhattan")
    repository.create(creditUnion)

    val praful = new User("Praful", "Manhattan")
    repository.createUser(praful)

    val userNode = graphDb.index.forNodes("nodes").get("user", praful.name).getSingle()

    val allCreditUnions = userNode.traverse(Traverser.Order.BREADTH_FIRST,
      StopEvaluator.END_OF_GRAPH,
      repository.getReturnEvaluator,
      "worksIn": RelationshipType,
      Direction.BOTH,
      "acceptsWorkingIn": RelationshipType,
      Direction.BOTH).toList
    allCreditUnions.size should equal(1)
    val foundUserNode = allCreditUnions.get(0)
    foundUserNode.getProperty("name") should be === ("Manhattan")
  }

  implicit def stringToRelationshipType(x: String): RelationshipType = DynamicRelationshipType.withName(x)

}