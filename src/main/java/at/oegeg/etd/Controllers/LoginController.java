package at.oegeg.etd.Controllers;

import at.oegeg.etd.DataTransferObjects.Request.LoginRequest;
import at.oegeg.etd.DataTransferObjects.Response.LoginResponse;
import at.oegeg.etd.RestServices.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
public class LoginController
{
    private final LoginService _loginService;

    // == methods ==
    @PostMapping()
    public LoginResponse Login(@RequestBody LoginRequest loginRequest)
    {
        return _loginService.Login(loginRequest);
    }

    @DeleteMapping
    public void Logout(@RequestHeader String authorization)
    {
        _loginService.Logout(authorization);
    }
}
