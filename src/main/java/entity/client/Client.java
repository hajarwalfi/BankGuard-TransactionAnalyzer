package entity.client;

public record Client(
        Long id,
        String name,
        String email
) {

    public Client(String name, String email) {
        this(null, name, email);
    }

    @Override
    public String toString() {
        return "Client{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}