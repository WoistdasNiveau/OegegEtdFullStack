package at.oegeg.etd.Repositories;

import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Entities.WorkEntity;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
public class WorkRepositoryTests
{
    @Mock
    private IUserEntityRepository _userEntityRepository;
    @Mock
    private IWorkRepository _workRepository;

    @Test
    public void testFindByResponsiblePersonAndSearchString()
    {
        // Mock data
        UserEntity user = UserEntity.builder()
                .identifier(UUID.randomUUID().toString())
                .name("test")
                .IsUserEnabled(true)
                .email("mail")
                .password("password")
                .build();
        String searchString = "test";

        // Mock UserRepository behavior
        when(_userEntityRepository.findById(anyLong())).thenReturn(java.util.Optional.of(user));

        // Mock WorkEntityRepository behavior
        List<WorkEntity> expectedResults = Collections.singletonList(new WorkEntity());
        when(_workRepository.findByResponsiblePersonAndSearchString(any(UserEntity.class), anyString()))
                .thenReturn(expectedResults);

        // Call the method
        List<WorkEntity> results = _workRepository.findByResponsiblePersonAndSearchString(user, searchString);

        // Verify the results
        assertThat(results).isEqualTo(expectedResults);
    }
}
