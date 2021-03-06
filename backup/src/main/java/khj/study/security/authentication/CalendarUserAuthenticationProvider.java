package khj.study.security.authentication;

import khj.study.security.authority.CalendarUserAuthorityUtils;
import khj.study.security.domain.CalendarUser;
import khj.study.security.service.CalendarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class CalendarUserAuthenticationProvider implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(CalendarUserAuthenticationProvider.class);
    private final CalendarService calendarService;

    @Autowired
    public CalendarUserAuthenticationProvider(final CalendarService calendarService) {
        if (calendarService == null) {
            throw new IllegalArgumentException("calendarService cannot be null");
        }
        this.calendarService = calendarService;
    }

//    @Override
//    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
//        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
//        String email = token.getName();
//        CalendarUser user = email == null ? null : calendarService.findUserByEmail(email);
//        if(user == null) {
//            throw new UsernameNotFoundException("Invalid username/password");
//        }
//        String password = user.getPassword();
//        if(!password.equals(token.getCredentials())) {
//            throw new BadCredentialsException("Invalid username/password");
//        }
//        Collection<? extends GrantedAuthority> authorities = CalendarUserAuthorityUtils.createAuthorities(user);
//        return new UsernamePasswordAuthenticationToken(user, password, authorities);
//    }
@Override
public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    DomainUsernamePasswordAuthenticationToken token = (DomainUsernamePasswordAuthenticationToken) authentication;
    String userName = token.getName();
    String domain = token.getDomain();
    String email = userName + "@" + domain;

    CalendarUser user = calendarService.findUserByEmail(email);
    logger.info("calendarUser: {}", user);

    if(user == null) {
        throw new UsernameNotFoundException("Invalid username/password");
    }
    String password = user.getPassword();
    if(!password.equals(token.getCredentials())) {
        throw new BadCredentialsException("Invalid username/password");
    }
    Collection<? extends GrantedAuthority> authorities = CalendarUserAuthorityUtils.createAuthorities(user);
    logger.info("authorities: {}", authorities);
    return new DomainUsernamePasswordAuthenticationToken(user, password, domain, authorities);
}

    @Override
    public boolean supports(final Class<?> authentication) {
        return DomainUsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
