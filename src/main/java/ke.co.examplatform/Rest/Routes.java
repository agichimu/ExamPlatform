package ke.co.examplatform.Rest;

import io.undertow.Handlers;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.util.Methods;
import ke.co.examplatform.QuerryManager.QueryManager;
import ke.co.examplatform.SchoolManagement.Classes.CreateClasses;
import ke.co.examplatform.SchoolManagement.Classes.DeleteClasses;
import ke.co.examplatform.SchoolManagement.Classes.GetClasses;
import ke.co.examplatform.SchoolManagement.Classes.UpdateClasses;
import ke.co.examplatform.SchoolManagement.Departments.CreateDepartments;
import ke.co.examplatform.SchoolManagement.Departments.DeleteDepartments;
import ke.co.examplatform.SchoolManagement.Departments.GetDepartments;
import ke.co.examplatform.SchoolManagement.Departments.UpdateDepartments;
import ke.co.examplatform.SchoolManagement.Subjects.CreateSubjects;
import ke.co.examplatform.SchoolManagement.Subjects.DeleteSubjects;
import ke.co.examplatform.SchoolManagement.Subjects.GetSubjects;
import ke.co.examplatform.SchoolManagement.Subjects.UpdateSubjects;
import ke.co.examplatform.Users.Guardians.CreateGuardian;
import ke.co.examplatform.Users.Guardians.DeleteGuardian;
import ke.co.examplatform.Users.Guardians.GetGuardian;
import ke.co.examplatform.Users.Guardians.GetGuardians;
import ke.co.examplatform.Users.Guardians.UpdateGuardians;
import ke.co.examplatform.Users.Guardians.guardianroles.CreateGuardianRoles;
import ke.co.examplatform.Users.Guardians.guardianroles.DeleteGuardianRoles;
import ke.co.examplatform.Users.Guardians.guardianroles.GetGuardianRoles;
import ke.co.examplatform.Users.Guardians.guardianroles.UpdateGuardianRoles;
import ke.co.examplatform.Users.Pupils.CreatePupils;
import ke.co.examplatform.Users.Pupils.DeletePupils;
import ke.co.examplatform.Users.Pupils.GetPupil;
import ke.co.examplatform.Users.Pupils.GetPupils;
import ke.co.examplatform.Users.Pupils.UpdatePupils;
import ke.co.examplatform.Users.Teachers.CreateTeachers;
import ke.co.examplatform.Users.Teachers.DeleteTeachers;
import ke.co.examplatform.Users.Teachers.GetTeachers;
import ke.co.examplatform.Users.Teachers.UpdateTeachers;
import ke.co.examplatform.examinations.Choices.CreateChoices;
import ke.co.examplatform.examinations.Choices.DeleteChoices;
import ke.co.examplatform.examinations.Choices.GetChoice;
import ke.co.examplatform.examinations.Choices.GetChoices;
import ke.co.examplatform.examinations.Choices.UpdateChoices;
import ke.co.examplatform.examinations.CreateExaminations;
import ke.co.examplatform.examinations.DeleteExaminations;
import ke.co.examplatform.examinations.GetExamination;
import ke.co.examplatform.examinations.GetExaminations;
import ke.co.examplatform.examinations.Questions.CreateQuestions;
import ke.co.examplatform.examinations.Questions.DeleteQuestions;
import ke.co.examplatform.examinations.Questions.GetQuestion;
import ke.co.examplatform.examinations.Questions.GetQuestions;
import ke.co.examplatform.examinations.Questions.UpdateQuestions;
import ke.co.examplatform.examinations.UpdateExaminations;

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
                .get("/{roleId}", new Dispatcher(new GetGuardian(queryManager)))
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
                .get("/{questionId}", new Dispatcher(new GetQuestion(queryManager)))
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
               .get("/{choiceId}", new Dispatcher(new GetChoice(queryManager)))
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
                .get("/{classId}", new Dispatcher(new GetClasses()))
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
                .get("/{departmentId}", new Dispatcher(new GetDepartments()))
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
                .get("/{subjectId}", new Dispatcher(new GetSubjects()))
                .post("", new BlockingHandler(new CreateSubjects(queryManager)))
                .add(Methods.PATCH, "/{subjectId}", new BlockingHandler(new UpdateSubjects(queryManager)))
                .delete("/{subjectId}", new Dispatcher(new DeleteSubjects(queryManager)))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }
}