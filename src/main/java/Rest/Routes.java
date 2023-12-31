package Rest;

import QuerryManager.QueryManager;
import SchoolManagement.Classes.CreateClasses;
import SchoolManagement.Classes.DeleteClasses;
import SchoolManagement.Classes.GetClasses;
import SchoolManagement.Classes.UpdateClasses;
import SchoolManagement.Departments.CreateDepartments;
import SchoolManagement.Departments.DeleteDepartments;
import SchoolManagement.Departments.GetDepartments;
import SchoolManagement.Departments.UpdateDepartments;
import SchoolManagement.Subjects.CreateSubjects;
import SchoolManagement.Subjects.DeleteSubjects;
import SchoolManagement.Subjects.GetSubjects;
import SchoolManagement.Subjects.UpdateSubjects;
import Users.Guardians.*;
import Users.Guardians.guardianroles.CreateGuardianRoles;
import Users.Guardians.guardianroles.DeleteGuardianRoles;
import Users.Guardians.guardianroles.GetGuardianRoles;
import Users.Guardians.guardianroles.UpdateGuardianRoles;
import Users.Pupils.*;
import Users.Teachers.CreateTeachers;
import Users.Teachers.DeleteTeachers;
import Users.Teachers.GetTeachers;
import Users.Teachers.UpdateTeachers;
import examinations.Choices.CreateChoices;
import examinations.Choices.DeleteChoices;
import examinations.Choices.GetChoices;
import examinations.Choices.UpdateChoices;
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
                .get("/{pupilId}", new Dispatcher(new GetPupil(queryManager)))
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
               .get("/{guardianId}", new Dispatcher(new GetGuardian(queryManager)))
                 .post("", new BlockingHandler(new CreateGuardian(queryManager)))
                .add(Methods.PATCH, "/{guardianId}", new BlockingHandler(new UpdateGuardians(queryManager)))
                 .delete("/{guardianId}", new Dispatcher(new DeleteGuardian(queryManager)))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }
    public static RoutingHandler GuardianRoles() {
        QueryManager queryManager = new QueryManager();
        return Handlers.routing()
                .get("", new Dispatcher(new GetGuardianRoles()))
                // .get("/{roleId}", new Dispatcher(new GetGuardian()))
                .post("", new BlockingHandler(new CreateGuardianRoles(queryManager)))
                .add(Methods.PATCH, "/{roleId}", new BlockingHandler(new UpdateGuardianRoles(queryManager)))
                .delete("/{roleId}", new Dispatcher(new DeleteGuardianRoles(queryManager)))
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
                .post("", new BlockingHandler(new CreateChoices(queryManager)))
                .add(Methods.PATCH, "/{choiceId}", new BlockingHandler(new UpdateChoices(queryManager)))
                .delete("/{choiceId}", new Dispatcher(new DeleteChoices(queryManager)))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }
    public static RoutingHandler Classes() {
        QueryManager queryManager = new QueryManager();
        return Handlers.routing()
                .get("", new Dispatcher(new GetClasses()))
                // .get("/{classId}", new Dispatcher(new GetClasses()))
                .post("", new BlockingHandler(new CreateClasses(queryManager)))
                .add(Methods.PATCH, "/{classId}", new BlockingHandler(new UpdateClasses(queryManager)))
                .delete("/{classId}", new Dispatcher(new DeleteClasses(queryManager)))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }
    public static RoutingHandler Departments() {
        QueryManager queryManager = new QueryManager();
        return Handlers.routing()
                .get("", new Dispatcher(new GetDepartments()))
                // .get("/{departmentId}", new Dispatcher(new GetDepartments()))
                .post("", new BlockingHandler(new CreateDepartments(queryManager)))
                .add(Methods.PATCH, "/{departmentId}", new BlockingHandler(new UpdateDepartments(queryManager)))
                .delete("/{departmentId}", new Dispatcher(new DeleteDepartments(queryManager)))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }
    public static RoutingHandler Subjects() {
        QueryManager queryManager = new QueryManager();
        return Handlers.routing()
                .get("", new Dispatcher(new GetSubjects()))
                // .get("/{subjectId}", new Dispatcher(new GetSubjects()))
                .post("", new BlockingHandler(new CreateSubjects(queryManager)))
                .add(Methods.PATCH, "/{subjectId}", new BlockingHandler(new UpdateSubjects(queryManager)))
                .delete("/{subjectId}", new Dispatcher(new DeleteSubjects(queryManager)))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }
}
