package stepbuilder;

import static com.amazonaws.services.stepfunctions.builder.StepFunctionBuilder.*;
import com.amazonaws.services.stepfunctions.builder.ErrorCodes;
import com.amazonaws.services.stepfunctions.builder.StateMachine;
import com.amazonaws.services.stepfunctions.builder.states.Branch;

public class StepBuilder {

    //arn:aws:lambda:us-east-1:425548863806:function:john-jobs-test-dev-submit-job

    private static final String ACCOUNT_ID = "425548863806";
    private static final String REGION = "us-east-1";

    private static final String LAMBDA_PREFIX = "john-jobs-test-dev-";

    class Lambdas {
        public static final String SUBMIT_JOB = "submit-job";
        public static final String CHECK_JOB_STATUS = "check-job-status";
        public static final String GET_TRANSCODES = "get-transcodes";
    }

    public static void main(String[] args) {

        StateMachine stateMachine = buildStateMachine();

        //StateMachine stateMachine = wrapBranch();

        System.out.println(stateMachine.toPrettyJson());
    }

    static StateMachine buildStateMachine() {
        final StateMachine stateMachine = stateMachine()
                .comment("Johns Test Stuff")
                .startAt("Submit Job")
                .state("Submit Job", taskState()
                        .resource(arnFor(Lambdas.SUBMIT_JOB))
                        .transition(next("Wait X Seconds")))
                .state("Wait X Seconds", waitState()
                        .waitFor(seconds(10))
                        .transition(next("Check Job Status")))
                .state("Check Job Status", taskState()
                        .resource(arnFor(Lambdas.CHECK_JOB_STATUS))
                        .inputPath("$.jobId")
                        .resultPath("$.status")
                        .transition(next("Job Complete?")))
                .state("Job Complete?", choiceState()
                        .choice(choice()
                                .transition(next("Job Succeeded"))
                                .condition(eq("$.status", "SUCCEEDED")))
                        .choice(choice()
                                .transition(next("Job Failed"))
                                .condition(eq("$.status", "FAILED")))
                        .defaultStateName("Wait X Seconds"))
                .state("Job Failed", failState()
                        .cause("Badness"))
                .state("Job Succeeded", passState()
                        .transition(next("Get Required Transcodes")))
                //branch
                .state("Get Required Transcodes", taskState()
                        .resource(arnFor(Lambdas.GET_TRANSCODES))
                        .resultPath("$.transcodes")
                        .transition(next("Parallel Transcodes")))
                .state("Parallel Transcodes", parallelState()
                        .branch(buildSANBranch())
                        .branch(buildWEBBranch())
                        .transition(next("Collect")))
                .state("Collect", passState()
                        .transition(end())).build();

        return stateMachine;
    }

//    private static Branch.Builder buildThing() {
//        return branch()
//                .startAt("Needs SAN Transcode")
//                .state("Needs SAN Transcode", choiceState()
//                        .choice(choice()
//                                .transition(next("SAN Transcode"))
//                                .condition(eq("$.transcodes.SAN", true)))
//                .state("SAN Transcode", passState()
//                        .transition(next("SAN Complete")))
//                .state("SAN Complete", passState()
//                        .transition(end()));
//    }

    static Branch.Builder buildSANBranch() {
        return branch()
                .startAt("Needs SAN Transcode")
                .state("Needs SAN Transcode",
                        choiceState()
                                .choice(choice()
                                        .condition(eq("$.transcodes.SAN", true))
                                        .transition(next("SAN Transcode")))
                                .choice(choice()
                                        .condition(eq("$.transcodes.SAN", false))
                                        .transition(next("SAN Complete")))
                                .defaultStateName("SAN Complete"))
                .state("SAN Transcode", passState()
                        .transition(next("SAN Complete")))
                .state("SAN Complete", passState()
                        .transition(end()));
    }

    static Branch.Builder buildWEBBranch() {
        return branch().startAt("Needs WEB Transcode")
                .state("Needs WEB Transcode",
                        choiceState()
                                .choice(choice()
                                        .condition(eq("$.transcodes.WEB", true))
                                        .transition(next("WEB Transcode")))
                                .choice(choice()
                                        .condition(eq("$.transcodes.WEB", false))
                                        .transition(next("WEB Complete")))
                                .defaultStateName("WEB Complete"))
                .state("WEB Transcode", passState()
                        .transition(next("WEB Complete")))
                .state("WEB Complete", passState()
                        .transition(end()));
    }

    private static String arnFor(String functionName) {
        return "arn:aws:lambda:" + REGION + ":" + ACCOUNT_ID + ":function:" + LAMBDA_PREFIX + functionName;
    }
}
