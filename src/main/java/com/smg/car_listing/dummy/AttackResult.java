package com.smg.car_listing.dummy;


import lombok.Getter;

public class AttackResult {

    public static class AttackResultBuilder {

        private boolean lessonCompleted;
        private PluginMessages messages;
        private Object[] feedbackArgs;
        private String feedbackResourceBundleKey;
        private String output;
        private Object[] outputArgs;
//        private AssignmentEndpoint assignment;
        private boolean attemptWasMade = false;

        public AttackResultBuilder(PluginMessages messages) {
            this.messages = messages;
        }

        public AttackResultBuilder lessonCompleted(boolean lessonCompleted) {
            this.lessonCompleted = lessonCompleted;
            this.feedbackResourceBundleKey = "lesson.completed";
            return this;
        }

        public AttackResultBuilder lessonCompleted(boolean lessonCompleted, String resourceBundleKey) {
            this.lessonCompleted = lessonCompleted;
            this.feedbackResourceBundleKey = resourceBundleKey;
            return this;
        }

        public AttackResultBuilder feedbackArgs(Object... args) {
            this.feedbackArgs = args;
            return this;
        }

        public AttackResultBuilder feedback(String resourceBundleKey) {
            this.feedbackResourceBundleKey = resourceBundleKey;
            return this;
        }

        public AttackResultBuilder output(String output) {
            this.output = output;
            return this;
        }

        public AttackResultBuilder outputArgs(Object... args) {
            this.outputArgs = args;
            return this;
        }

        public AttackResultBuilder attemptWasMade() {
            this.attemptWasMade = true;
            return this;
        }

        public AttackResult build() {
            return new AttackResult(
                    lessonCompleted,
                    messages.getMessage(feedbackResourceBundleKey, feedbackArgs),
                    messages.getMessage(output, output, outputArgs),
//                    assignment.getClass().getSimpleName(),
                    attemptWasMade);
        }

//        public AttackResultBuilder assignment(AssignmentEndpoint assignment) {
////            this.assignment = assignment;
//            return this;
//        }
    }

    @Getter private boolean lessonCompleted;
    @Getter private String feedback;
    @Getter private String output;
//    @Getter private final String assignment;
    @Getter private boolean attemptWasMade;

    public AttackResult(
            boolean lessonCompleted,
            String feedback,
            String output,
//            String assignment,
            boolean attemptWasMade) {
        this.lessonCompleted = lessonCompleted;
        this.feedback = feedback;
        this.output = output;
//        this.assignment = assignment;
        this.attemptWasMade = attemptWasMade;
    }

    public static AttackResultBuilder builder(PluginMessages messages) {
        return new AttackResultBuilder(messages);
    }

    public boolean assignmentSolved() {
        return lessonCompleted;
    }
}
