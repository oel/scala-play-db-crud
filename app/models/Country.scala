package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import scala.language.postfixOps

case class Country(id: Option[Long] = None, name: String)

object Country {

    /**
     * Parse a Country from a ResultSet
     */
    val simple = {
        get[Option[Long]]("country.id") ~
        get[String]("country.name") map {
            case id ~ name => Country(id, name)
        }
    }

    /**
     * Construct the Map[String,String] needed to fill a select options set.
     */
    def options: Seq[(String,String)] = DB.withConnection { implicit connection =>
        SQL("select * from country order by name").as(Country.simple *).
            foldLeft[Seq[(String, String)]](Nil) { (cs, c) => 
                c.id.fold(cs) { id => cs :+ (id.toString -> c.name) }
            }
    }

}
