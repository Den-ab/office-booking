package ru.otus.pw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.pw.config.SecurityConfig;
import ru.otus.pw.controllers.request_dtos.AreaDTO;
import ru.otus.pw.controllers.response_dtos.AreaResponseDTO;
import ru.otus.pw.services.AppUserDetailsService;
import ru.otus.pw.services.AreaService;
import ru.otus.pw.services.BookingService;
import ru.otus.pw.services.DeskService;
import ru.otus.pw.services.JwtService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AreaController.class, DeskController.class, BookingController.class})
@Import(SecurityConfig.class)
class ApiSecurityAndErrorHandlingTest {

    private static final String AREA_JSON = "{\"name\": \"Open space\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AreaService areaService;

    @MockitoBean
    private DeskService deskService;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    @Test
    @DisplayName("Anonymous requests are rejected")
    void anonymousRequestIsRejected() throws Exception {
        mockMvc.perform(get("/areas"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("A signed-in user can list areas")
    void userCanListAreas() throws Exception {
        when(areaService.findAll()).thenReturn(List.of(new AreaResponseDTO("1", "Open space")));

        mockMvc.perform(get("/areas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Open space"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("A regular user cannot create areas")
    void userCannotCreateArea() throws Exception {
        mockMvc.perform(post("/areas").contentType(MediaType.APPLICATION_JSON).content(AREA_JSON))
            .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/areas/", "/desks/", "/bookings/"})
    @WithMockUser(roles = "USER")
    @DisplayName("A regular user cannot delete areas, desks or bookings")
    void userCannotDelete(String path) throws Exception {
        mockMvc.perform(delete(path + UUID.randomUUID()))
            .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/areas/", "/desks/", "/bookings/"})
    @WithMockUser(roles = "ADMIN")
    @DisplayName("An admin can delete areas, desks and bookings")
    void adminCanDelete(String path) throws Exception {
        mockMvc.perform(delete(path + UUID.randomUUID()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("An admin can create areas")
    void adminCanCreateArea() throws Exception {
        when(areaService.create(any(AreaDTO.class))).thenReturn(new AreaResponseDTO("1", "Open space"));

        mockMvc.perform(post("/areas").contentType(MediaType.APPLICATION_JSON).content(AREA_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Open space"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Invalid input returns 400 with the failing field")
    void invalidAreaReturnsFieldErrors() throws Exception {
        mockMvc.perform(post("/areas").contentType(MediaType.APPLICATION_JSON).content("{\"name\": \"\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("A booking conflict is returned as 409")
    void bookingConflictReturns409() throws Exception {
        when(bookingService.bookDesk(any(UUID.class), any(UUID.class)))
            .thenThrow(new IllegalStateException("You have already booked this desk for today."));
        String body = "{\"userId\": \"%s\", \"deskId\": \"%s\"}".formatted(UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("You have already booked this desk for today."));
    }
}
