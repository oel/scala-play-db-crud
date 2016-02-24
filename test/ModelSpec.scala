import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class ModelSpec extends Specification {

    import models._

    // -- Date helpers

    def dateIs(date: java.util.Date, str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) == str

    // --

    "Song" should {

        "be retrieved by id" in {
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

                val Some(altRock) = Song.findById(1007)

                altRock.name must equalTo("Street Spirit")
                altRock.released must beSome.which(dateIs(_, "1995-03-13"))    

            }
        }

        "be listed along its musicians" in {
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

                val songs = Song.list()

                songs.total must equalTo(33)
                songs.items must have length(10)

            }
        }

        "be updated if needed" in {
            running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

                Song.update(1007, Song(name="Street Spirit (Fade Out)", released=None, musicianId=Some(206)))

                val Some(altRock) = Song.findById(1007)

                altRock.name must equalTo("Street Spirit (Fade Out)")
                altRock.released must beNone

            }
        }

    }

}
