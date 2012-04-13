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

@RunWith(classOf[JUnitRunner])
class CreditUnionRepositorySpec extends FunSuite with BeforeAndAfter {

  var graphDb: GraphDatabaseService = _
  
  before {
    graphDb = new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder().newGraphDatabase()
  }
  
  after {
    graphDb.shutdown()
  }
  
  test("should create credit union") {
    val repository = new CreditUnionRepository(graphDb)
    val creditUnion = new CreditUnion("Manhattan Credit Union", "Manhattan")
    repository.create(creditUnion)
    
    val creditUnionNode = graphDb.index.forNodes("nodes").get("creditUnion", creditUnion.name).getSingle()
    assert(creditUnionNode.getProperty("name") === "Manhattan Credit Union")

    val locationNode = graphDb.index.forNodes("nodes").get("location", creditUnion.acceptsWorkingIn).getSingle()
    assert(locationNode.getProperty("name") === "Manhattan")
    
    val locationNodes = creditUnionNode.traverse(Traverser.Order.BREADTH_FIRST,
    							StopEvaluator.END_OF_GRAPH,
    							ReturnableEvaluator.ALL,
    							"acceptsWorkingIn" : RelationshipType,
    							Direction.OUTGOING)
    			
    assert(locationNodes.getAllNodes().toList === List(creditUnionNode, locationNode))
  }
  
  implicit def stringToRelationshipType(x: String): RelationshipType = DynamicRelationshipType.withName(x)

}