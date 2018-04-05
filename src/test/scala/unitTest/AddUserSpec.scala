package unitTest

import org.scalatest._

object DAO {
}

trait DAO {
}

class DAOMock extends DAO {
}

class AddUser(implicit val dao: DAO) {
}

class AddUserSpec extends fixture.WordSpec with Matchers {

  "AddUser" when {

    "validation" should {
      "throw exception if email is empty" in {implicit dao =>
        pending
      }

      "throw exception if name is empty" in {implicit dao =>
        pending
      }

      "throw exception if email is duplicate" in {implicit dao =>
        pending
      }

    }

    "save to database" should {
      "set createTime / updateTime correctly" in { implicit dao =>
        pending
      }

      "generate journal object" in { implicit dao =>
        pending
      }

      "call DAO object to save" in { implicit dao =>
        pending
      }
    }
  }

  type FixtureParam = DAO

  override def withFixture(test: OneArgTest) = {
    val dao = new DAOMock
    test(dao)
  }

}
