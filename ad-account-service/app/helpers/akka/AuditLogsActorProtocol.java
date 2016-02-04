package helpers.akka;

public class AuditLogsActorProtocol {

    public static class RegisterAuditLog {

        public Long requestNumber;
        public String userId;
        public String application;
        public String executor;
        public String action;

        public RegisterAuditLog(Long requestNumber, String userId, String application, String executor, String action) {
            this.requestNumber = requestNumber;
            this.userId = userId;
            this.application = application;
            this.executor = executor;
            this.action = action;
        }

        @Override
        public String toString() {
            return "RegisterAuditLog{" +
                    "requestNumber=" + requestNumber +
                    ", userId='" + userId + '\'' +
                    ", application='" + application + '\'' +
                    ", executor='" + executor + '\'' +
                    ", action='" + action + '\'' +
                    '}';
        }
    }

}
