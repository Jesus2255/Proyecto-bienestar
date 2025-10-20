package com.bienestarproyect.Bienestar.service;

import com.bienestarproyect.Bienestar.entity.Factura;
import com.bienestarproyect.Bienestar.repository.FacturaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FacturaServiceTest {

    @Mock
    private FacturaRepository repo;

    @InjectMocks
    private FacturaService service;

    @Test
    public void crear_debe_setear_fecha_y_guardar() {
        Factura f = new Factura();
        f.setTotal(null);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Factura result = service.crear(f);

        ArgumentCaptor<Factura> captor = ArgumentCaptor.forClass(Factura.class);
        verify(repo, times(1)).save(captor.capture());
        Factura saved = captor.getValue();

        assertNotNull(saved.getFecha());
        assertEquals(saved.getFecha(), result.getFecha());
    }
}