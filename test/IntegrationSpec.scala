import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

import org.fluentlenium.core.filter.FilterConstructor._

@RunWith(classOf[JUnitRunner])
class IntegrationSpec extends Specification {

    "Application" should {

        "work from within a browser" in {
            running(TestServer(3333), HTMLUNIT) { browser =>
                browser.goTo("http://localhost:3333/")

                browser.$("header h1").first.getText must equalTo("Play PoC — Music Database")
                browser.$("section h1").first.getText must equalTo("33 songs found")

                browser.$("#pagination li.current").first.getText must equalTo("Displaying 1 to 10 of 33")

                browser.$("#pagination li.next a").click()

                browser.$("#pagination li.current").first.getText must equalTo("Displaying 11 to 20 of 33")
                browser.$("#searchbox").text("Love")
                browser.$("#searchsubmit").click()

                browser.$("section h1").first.getText must equalTo("2 songs found")
                browser.$("a", withText("Love Of My Life")).click()

                browser.$("section h1").first.getText must equalTo("Edit song")

                browser.$("#released").text("xxx")
                browser.$("input.primary").click()

                browser.$("div.error").size must equalTo(1)
                browser.$("div.error label").first.getText must equalTo("Released date")

                browser.$("#released").text("")
                browser.$("input.primary").click()

                browser.$("section h1").first.getText must equalTo("33 songs found")
                browser.$(".alert-message").first.getText must equalTo("Done! Song Love Of My Life has been updated")

                browser.$("#searchbox").text("Love")
                browser.$("#searchsubmit").click()

                browser.$("a", withText("Love Of My Life")).click()
                browser.$("input.danger").click()

                browser.$("section h1").first.getText must equalTo("32 songs found")
                browser.$(".alert-message").first.getText must equalTo("Done! Song has been deleted")

                browser.$("#searchbox").text("Love")
                browser.$("#searchsubmit").click()

                browser.$("section h1").first.getText must equalTo("One song found")

            }
        }

    }

}
