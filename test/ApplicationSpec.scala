import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

    import models._

    // -- Date helpers

    def dateIs(date: java.util.Date, str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) == str

    // --

    val applicationController = new controllers.SongController()

    "SongController" should {

        "redirect to the song list on /" in {

            val result = applicationController.index(FakeRequest())

            status(result) must equalTo(SEE_OTHER)
            redirectLocation(result) must beSome.which(_ == "/songs")

        }

        "list songs on the the first page" in {
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

                val result = applicationController.list(0, 2, "")(FakeRequest())

                status(result) must equalTo(OK)
                contentAsString(result) must contain("33 songs found")

            }            
        }

        "filter song by name" in {
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

                val result = applicationController.list(0, 2, "you")(FakeRequest())

                status(result) must equalTo(OK)
                contentAsString(result) must contain("3 songs found")

            }            
        }

        "create new song" in {
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

                val badResult = applicationController.save(FakeRequest())

                status(badResult) must equalTo(BAD_REQUEST)

                val badDateFormat = applicationController.save(
                    FakeRequest().withFormUrlEncodedBody("name" -> "Foo Bar", "released" -> "bad bad bad", "musician" -> "200")
                )

                status(badDateFormat) must equalTo(BAD_REQUEST)
                contentAsString(badDateFormat) must contain("""<option value="200" selected="selected">Pink Floyd</option>""")
                contentAsString(badDateFormat) must contain("""<input type="date" id="released" name="released" value="bad bad bad" />""")
                contentAsString(badDateFormat) must contain("""<input type="text" id="name" name="name" value="Foo Bar" />""")


                val result = applicationController.save(
                    FakeRequest().withFormUrlEncodedBody("name" -> "Foo Bar", "released" -> "2000-01-01", "musician" -> "200")
                )

                status(result) must equalTo(SEE_OTHER)
                redirectLocation(result) must beSome.which(_ == "/songs")
                flash(result).get("success") must beSome.which(_ == "Song Foo Bar has been created")

                val list = applicationController.list(0, 2, "Foo Bar")(FakeRequest())

                status(list) must equalTo(OK)
                contentAsString(list) must contain("One song found")

            }
        }

    }

}
