package Rest;

import QuerryManager.QueryManager;
import Users.Guardians.CreateGuardian;
import Users.Guardians.DeleteGuardian;
import Users.Guardians.GetGuardians;
import Users.Guardians.UpdateGuardians;
import Users.Pupils.CreatePupils;
import Users.Pupils.DeletePupils;
import Users.Pupils.GetPupils;
import Users.Pupils.UpdatePupils;
import Users.Teachers.CreateTeachers;
import Users.Teachers.DeleteTeachers;
import Users.Teachers.GetTeachers;
import Users.Teachers.UpdateTeachers;
import examinations.choices.GetChoices;
import examinations.*;
import examinations.Questions.CreateQuestions;
import examinations.Questions.DeleteQuestions;
import examinations.Questions.GetQuestions;
import examinations.Questions.UpdateQuestions;
import io.undertow.Handlers;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.util.Methods;

public class Routes {
    public static RoutingHandler examinations() {
        QueryManager queryManager = new QueryManager();

        return Handlers.routing()
                .get("", new Dispatcher(new GetExaminations()))
                .get("/{examinationId}", new Dispatcher(new GetExamination(queryManager)))
                .post("", new BlockingHandler(new CreateExaminations(queryManager)))
                .add(Methods.PATCH, "/{examinationId}", new BlockingHandler(new UpdateExaminations(queryManager)))
                .delete("/{examinationId}", new Dispatcher(new DeleteExaminations(queryManager)))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

    public static RoutingHandler Pupils() {
        QueryManager queryManager = new QueryManager();

        return Handlers.routing()
                .get("", new Dispatcher(new GetPupils()))
                .post("", new BlockingHandler(new CreatePupils(queryManager)))
                .add(Methods.PATCH, "/{pupilId}", new BlockingHandler(new UpdatePupils(queryManager)))
                .delete("/{pupilId}", new Dispatcher(new DeletePupils(queryManager)))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }


    public static RoutingHandler Teachers() {
        QueryManager queryManager = new QueryManager();
        return Handlers.routing()
                .get("", new Dispatcher(new GetTeachers()))
                 .post("", new BlockingHandler(new CreateTeachers(queryManager)))
                 .add(Methods.PATCH, "/{teacherId}", new BlockingHandler(new UpdateTeachers(queryManager)))
                 .delete("/{teacherId}", new Dispatcher(new DeleteTeachers(queryManager)))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

    public static RoutingHandler Guardians() {
        QueryManager queryManager = new QueryManager();
        return Handlers.routing()
                .get("", new Dispatcher(new GetGuardians()))
               // .get("/{guardian_id}", new Dispatcher(new GetGuardian()))
                 .post("", new BlockingHandler(new CreateGuardian(queryManager)))
                .add(Methods.PATCH, "/{guardianId}", new BlockingHandler(new UpdateGuardians(queryManager)))
                 .delete("/{guardianId}", new Dispatcher(new DeleteGuardian(queryManager)))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }
    public static RoutingHandler Questions() {
        QueryManager queryManager = new QueryManager();
        return Handlers.routing()
                .get("", new Dispatcher(new GetQuestions()))
                //.get("/{questionId}", new Dispatcher(new GetQuestion()))
                .post("", new BlockingHandler(new CreateQuestions(queryManager)))
                .add(Methods.PATCH, "/{questionId}", new BlockingHandler(new UpdateQuestions(queryManager)))
                .delete("/{questionId}", new Dispatcher(new DeleteQuestions(queryManager)))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }
    public static RoutingHandler Choices() {
        QueryManager queryManager = new QueryManager();
        return Handlers.routing()
                .get("", new Dispatcher(new GetChoices()))
               // .get("/{choiceId}", new Dispatcher(new GetChoice()))
                .post("", new BlockingHandler(new choices.CreateChoices(queryManager)))
                //.add(Methods.PATCH, "/{choiceId}", new BlockingHandler(new UpdateChoices(queryManager)))
               // .delete("/{choiceId}", new Dispatcher(new DeleteChoice(queryManager)))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

}
