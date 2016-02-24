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
 * Manage a database of musicians
 */
class MusicianController extends Controller { 

    /**
     * This result directly redirect to the application home.
     */
    val Home = Redirect(routes.MusicianController.list(0, 2, ""))

    /**
     * Describe the musician form (used in both edit and create screens).
     */ 
    val musicianForm = Form(
        mapping(
            "id" -> ignored(None:Option[Long]),
            "name" -> nonEmptyText,
            "country" -> optional(longNumber)
        )(Musician.apply)(Musician.unapply)
    )

    // -- Actions

    /**
     * Handle default path requests, redirect to musicians list
     */    
    def index = Action { Home }

    /**
     * Display the paginated list of musicians.
     *
     * @param page Current page number (starts from 0)
     * @param orderBy Column to be sorted
     * @param filter Filter applied on musician names
     */
    def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
        Ok(html.listMusicians(
            Musician.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")), orderBy, filter)
        )
    }

    /**
     * Display the 'edit form' of a existing Musician.
     *
     * @param id Id of the musician to edit
     */
    def edit(id: Long) = Action { implicit request =>
        Musician.findById(id).map { musician =>
            Ok(html.editMusician(id, musicianForm.fill(musician), Country.options))
        }.getOrElse(NotFound)
    }

    /**
     * Handle the 'edit form' submission 
     *
     * @param id Id of the musician to edit
     */
    def update(id: Long) = Action { implicit request =>
        musicianForm.bindFromRequest.fold(
            formWithErrors => BadRequest(html.editMusician(id, formWithErrors, Country.options)),
            musician => {
                Musician.update(id, musician)
                Home.flashing("success" -> "Musician %s has been updated".format(musician.name))
            }
        )
    }

    /**
     * Display the 'new musician form'.
     */
    def create = Action { implicit request =>
        Ok(html.createMusician(musicianForm, Country.options))
    }

    /**
     * Handle the 'new musician form' submission.
     */
    def save = Action { implicit request =>
        musicianForm.bindFromRequest.fold(
            formWithErrors => BadRequest(html.createMusician(formWithErrors, Country.options)),
            musician => {
                Musician.insert(musician)
                Home.flashing("success" -> "Musician %s has been created".format(musician.name))
            }
        )
    }

    /**
     * Handle musician deletion.
     */
    def delete(id: Long) = Action { implicit request =>
        Musician.delete(id)
        Home.flashing("success" -> "Musician has been deleted")
    }

}

