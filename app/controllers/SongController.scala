package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import anorm._

import views._
import models._

/**
 * Manage a database of songs
 */
class SongController extends Controller { 

    /**
     * This result directly redirect to the application home.
     */
    val Home = Redirect(routes.SongController.list(0, 2, ""))

    /**
     * Describe the song form (used in both edit and create screens).
     */ 
    val songForm = Form(
        mapping(
            "id" -> ignored(None:Option[Long]),
            "name" -> nonEmptyText,
            "released" -> optional(date("yyyy-MM-dd")),
            "musician" -> optional(longNumber)
        )(Song.apply)(Song.unapply)
    )

    // -- Actions

    /**
     * Handle default path requests, redirect to songs list
     */    
    def index = Action { Home }

    /**
     * Display the paginated list of songs.
     *
     * @param page Current page number (starts from 0)
     * @param orderBy Column to be sorted
     * @param filter Filter applied on song names
     */
    def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
        Ok(html.listSongs(
            Song.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")), orderBy, filter)
        )
    }

    /**
     * Display the 'edit form' of a existing Song.
     *
     * @param id Id of the song to edit
     */
    def edit(id: Long) = Action { implicit request =>
        Song.findById(id).map { song =>
            Ok(html.editSong(id, songForm.fill(song), Musician.options))
        }.getOrElse(NotFound)
    }

    /**
     * Handle the 'edit form' submission 
     *
     * @param id Id of the song to edit
     */
    def update(id: Long) = Action { implicit request =>
        songForm.bindFromRequest.fold(
            formWithErrors => BadRequest(html.editSong(id, formWithErrors, Musician.options)),
            song => {
                Song.update(id, song)
                Home.flashing("success" -> "Song %s has been updated".format(song.name))
            }
        )
    }

    /**
     * Display the 'new song form'.
     */
    def create = Action { implicit request =>
        Ok(html.createSong(songForm, Musician.options))
    }

    /**
     * Handle the 'new song form' submission.
     */
    def save = Action { implicit request =>
        songForm.bindFromRequest.fold(
            formWithErrors => BadRequest(html.createSong(formWithErrors, Musician.options)),
            song => {
                Song.insert(song)
                Home.flashing("success" -> "Song %s has been created".format(song.name))
            }
        )
    }

    /**
     * Handle song deletion.
     */
    def delete(id: Long) = Action { implicit request =>
        Song.delete(id)
        Home.flashing("success" -> "Song has been deleted")
    }

}
