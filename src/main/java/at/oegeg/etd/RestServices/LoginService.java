package at.oegeg.etd.RestServices;

import at.oegeg.etd.DataTransferObjects.Request.LoginRequest;
import at.oegeg.etd.DataTransferObjects.Response.LoginResponse;
import at.oegeg.etd.Entities.TokenBlackList;
import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Exceptions.EntityNotFoundException;
import at.oegeg.etd.Exceptions.LoginFailedException;
import at.oegeg.etd.Repositories.ITokenBlackListRepository;
import at.oegeg.etd.Repositories.IUserEntityRepository;
import at.oegeg.etd.Security.Services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService
{
    private final IUserEntityRepository _userEntityRepository;
    private final ITokenBlackListRepository _blacklistedTokenRepository;
    private final PasswordEncoder _passwordEncoder;
    private final JwtService _jwtService;
    public LoginResponse Login(LoginRequest request)
    {
        UserEntity user = _userEntityRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(request.getIdentifier())
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class, request.getIdentifier()));
        String pw = _passwordEncoder.encode(request.getPassword());
        if(!_passwordEncoder.matches(request.getPassword(), user.getPassword()))
        {
            throw new LoginFailedException(request.getIdentifier());
        }
        LoginResponse response = LoginResponse.builder()
                .token(_jwtService.GenerateToken(user))
                .build();
        return response;
    }

    public void Logout(String auth)
    {
        _blacklistedTokenRepository.save(TokenBlackList.builder()
                .token(auth.substring(7))
                .build());
    }
}
