package helpers.akka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class AdAccountsActorProtocol {

    public static class GetAllAdAccounts {
        @Override
        public String toString() {
            return "GetAllAdAccounts";
        }
    }

    public static class GetAdAccountById {
        public String id;

        public GetAdAccountById() {
        }

        public GetAdAccountById(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "GetAdAccountById{" + id + '}';
        }
    }

    public static class CreateAdAccount {
        public String firstName;
        public String lastName;
        public String id;
        public String email;

        public CreateAdAccount() {
        }

        public CreateAdAccount(String firstName, String lastName, String id, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.id = id;
            this.email = email;
        }

        @Override
        public String toString() {
            return "CreateAdAccount{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", id='" + id + '\'' +
                    ", email='" + email + '\'' +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DeleteAdAccount {
        public String firstName;
        public String lastName;
        public String id;
        public String email;

        public DeleteAdAccount() {
        }

        public DeleteAdAccount(String firstName, String lastName, String id, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.id = id;
            this.email = email;
        }

        @Override
        public String toString() {
            return "DeleteAdAccount{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", id='" + id + '\'' +
                    ", email='" + email + '\'' +
                    '}';
        }
    }

}
