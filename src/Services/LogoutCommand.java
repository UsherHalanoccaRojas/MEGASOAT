package Services;

public class LogoutCommand implements Command {
    @Override
    public void execute() {
        AuthService.logout();
    }
}

