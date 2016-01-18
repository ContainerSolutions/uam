package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class AccountService extends Controller {

     public Result index() {
        return ok("this is Some default Ok response");
    }

}
