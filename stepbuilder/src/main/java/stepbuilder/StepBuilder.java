package stepbuilder;

import static com.amazonaws.services.stepfunctions.builder.StepFunctionBuilder.*;
import com.amazonaws.services.stepfunctions.builder.ErrorCodes;
import com.amazonaws.services.stepfunctions.builder.StateMachine;

public class StepBuilder {

    //arn:aws:lambda:us-east-1:425548863806:function:john-jobs-test-dev-submit-job

    private static final String ACCOUNT_ID = "425548863806";
    private static final String REGION = "us-east-1";

    class Lambdas {
        public static final String SUBMIT_JOB = "john-jobs-test-dev-submit-job";
        public static final String CHECK_JOB_STATUS = "john-jobs-test-dev-check-job-status";
    }

    public static void main(String[] args) {
        final StateMachine stateMachine = stateMachine()
                .comment("Johns Test Stuff")
                .startAt("Submit Job")
                .state("Submit Job", taskState()
                        .resource(arnFor(Lambdas.SUBMIT_JOB))
                        .resultPath("$.jobId")
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
                        .transition(end())).build();
        System.out.println(stateMachine.toPrettyJson());
    }

    private static String arnFor(String functionName) {
        return "arn:aws:lambda:" + REGION + ":" + ACCOUNT_ID + ":function:" + functionName;
    }
}
