package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import scala.language.postfixOps

case class Song(id: Option[Long] = None, name: String, released: Option[Date], musicianId: Option[Long])

object Song {

    // -- Parsers

    /**
     * Parse a Song from a ResultSet
     */
    val simple = {
        get[Option[Long]]("song.id") ~
        get[String]("song.name") ~
        get[Option[Date]]("song.released") ~
        get[Option[Long]]("song.musician_id") map {
            case id ~ name ~ released ~ musicianId => Song(id, name, released, musicianId)
        }
    }

    /**
     * Parse a (Song,Musician) from a ResultSet
     */
    val withMusician = Song.simple ~ (Musician.simple ?) map {
        case song ~ musician => (song,musician)
    }

    // -- Queries

    /**
     * Retrieve a song from the id.
     */
    def findById(id: Long): Option[Song] = {
        DB.withConnection { implicit connection =>
            SQL("select * from song where id = {id}").on('id -> id).as(Song.simple.singleOpt)
        }
    }

    /**
     * Return a page of (Song,Musician).
     *
     * @param page Page to display
     * @param pageSize Number of songs per page
     * @param orderBy Song property used for sorting
     * @param filter Filter applied on the name column
     */
    def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Song, Option[Musician])] = {

        val offset = pageSize * page

        DB.withConnection { implicit connection =>

            val songs = SQL(
                """
                    select * from song 
                    left join musician on song.musician_id = musician.id
                    where song.name like {filter}
                    order by {orderBy} nulls last
                    limit {pageSize} offset {offset}
                """
            ).on(
                'pageSize -> pageSize, 
                'offset -> offset,
                'filter -> filter,
                'orderBy -> orderBy
            ).as(Song.withMusician *)

            val totalRows = SQL(
                """
                    select count(*) from song 
                    left join musician on song.musician_id = musician.id
                    where song.name like {filter}
                """
            ).on(
                'filter -> filter
            ).as(scalar[Long].single)

            Page(songs, page, offset, totalRows)

        }

    }

    /**
     * Update a song.
     *
     * @param id The song id
     * @param song The song values.
     */
    def update(id: Long, song: Song) = {
        DB.withConnection { implicit connection =>
            SQL(
                """
                    update song
                    set name = {name}, released = {released}, musician_id = {musician_id}
                    where id = {id}
                """
            ).on(
                'id -> id,
                'name -> song.name,
                'released -> song.released,
                'musician_id -> song.musicianId
            ).executeUpdate()
        }
    }

    /**
     * Insert a new song.
     *
     * @param song The song values.
     */
    def insert(song: Song) = {
        DB.withConnection { implicit connection =>
            SQL(
                """
                    insert into song values (
                        (select next value for song_seq), 
                        {name}, {released}, {musician_id}
                    )
                """
            ).on(
                'name -> song.name,
                'released -> song.released,
                'musician_id -> song.musicianId
            ).executeUpdate()
        }
    }

    /**
     * Delete a song.
     *
     * @param id Id of the song to delete.
     */
    def delete(id: Long) = {
        DB.withConnection { implicit connection =>
            SQL("delete from song where id = {id}").on('id -> id).executeUpdate()
        }
    }

}

