package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import scala.language.postfixOps

case class Musician(id: Option[Long] = None, name: String, countryId: Option[Long])

object Musician {

    /**
     * Parse a Musician from a ResultSet
     */
    val simple = {
        get[Option[Long]]("musician.id") ~
        get[String]("musician.name") ~
        get[Option[Long]]("musician.country_id") map {
            case id ~ name ~ countryId => Musician(id, name, countryId)
        }
    }

    /**
     * Parse a (Musician,Country) from a ResultSet
     */
    val withCountry = Musician.simple ~ (Country.simple ?) map {
        case musician~country => (musician,country)
    }

    // -- Queries

    /**
     * Retrieve a musician from the id.
     */
    def findById(id: Long): Option[Musician] = {
        DB.withConnection { implicit connection =>
            SQL("select * from musician where id = {id}").on('id -> id).as(Musician.simple.singleOpt)
        }
    }

    /**
     * Return a page of (Musician,Country).
     *
     * @param page Page to display
     * @param pageSize Number of musicians per page
     * @param orderBy Musician property used for sorting
     * @param filter Filter applied on the name column
     */
    def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Musician, Option[Country])] = {

        val offset = pageSize * page

        DB.withConnection { implicit connection =>

            val musicians = SQL(
                """
                    select * from musician 
                    left join country on musician.country_id = country.id
                    where musician.name like {filter}
                    order by {orderBy} nulls last
                    limit {pageSize} offset {offset}
                """
            ).on(
                'pageSize -> pageSize, 
                'offset -> offset,
                'filter -> filter,
                'orderBy -> orderBy
            ).as(Musician.withCountry *)

            val totalRows = SQL(
                """
                    select count(*) from musician 
                    left join country on musician.country_id = country.id
                    where musician.name like {filter}
                """
            ).on(
                'filter -> filter
            ).as(scalar[Long].single)

            Page(musicians, page, offset, totalRows)

        }

    }

    /**
     * Update a musician.
     *
     * @param id The musician id
     * @param musician The musician values.
     */
    def update(id: Long, musician: Musician) = {
        DB.withConnection { implicit connection =>
            SQL(
                """
                    update musician
                    set name = {name}, country_id = {country_id}
                    where id = {id}
                """
            ).on(
                'id -> id,
                'name -> musician.name,
                'country_id -> musician.countryId
            ).executeUpdate()
        }
    }

    /**
     * Insert a new musician.
     *
     * @param musician The musician values.
     */
    def insert(musician: Musician) = {
        DB.withConnection { implicit connection =>
            SQL(
                """
                    insert into musician values (
                        (select next value for musician_seq), 
                        {name}, {country_id}
                    )
                """
            ).on(
                'name -> musician.name,
                'country_id -> musician.countryId
            ).executeUpdate()
        }
    }

    /**
     * Delete a musician.
     *
     * @param id Id of the musician to delete.
     */
    def delete(id: Long) = {
        DB.withConnection { implicit connection =>
            SQL("delete from musician where id = {id}").on('id -> id).executeUpdate()
        }
    }

    /**
     * Construct the Map[String,String] needed to fill a select options set.
     */
    def options: Seq[(String,String)] = DB.withConnection { implicit connection =>
        SQL("select * from musician order by name").as(Musician.simple *).
            foldLeft[Seq[(String, String)]](Nil) { (cs, c) => 
                c.id.fold(cs) { id => cs :+ (id.toString -> c.name) }
            }
    }

}
