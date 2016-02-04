package helpers.akka;

public class AdAccountStorageActorProtocol {
    public static class SaveAdAccountInfo {
        public String id;
        public String firstName;
        public String lastName;
        public String email;

        public SaveAdAccountInfo() {
        }

        public SaveAdAccountInfo(String id, String firstName, String lastName, String email) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }
    }

    public static class DeleteAdAccountInfo {
        public final String id;

        public DeleteAdAccountInfo(String id) {
            this.id = id;
        }
    }



}
