package com.smg.car_listing.dummy;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AssignmentEndpoint {

    @Getter @Autowired private PluginMessages messages;

    /**
     * Convenience method for create a successful result:
     *
     * <p>- Assignment is set to solved - Feedback message is set to 'assignment.solved'
     *
     * <p>Of course you can overwrite these values in a specific lesson
     *
     * @return a builder for creating a result from a lesson
     * @param assignment
     */
    protected AttackResult.AttackResultBuilder success(AssignmentEndpoint assignment) {
        return AttackResult.builder(messages)
                .lessonCompleted(true)
                .attemptWasMade()
                .feedback("assignment.solved");
//                .assignment(assignment);
    }

    /**
     * Convenience method for create a failed result:
     *
     * <p>- Assignment is set to not solved - Feedback message is set to 'assignment.not.solved'
     *
     * <p>Of course you can overwrite these values in a specific lesson
     *
     * @return a builder for creating a result from a lesson
     * @param assignment
     */
    protected AttackResult.AttackResultBuilder failed(AssignmentEndpoint assignment) {
        return AttackResult.builder(messages)
                .lessonCompleted(false)
                .attemptWasMade()
                .feedback("assignment.not.solved");
//                .assignment(assignment);
    }
}
