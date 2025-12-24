package com.trans.asgard.domain.historiquevente.service;

import com.trans.asgard.domain.Entrepot.repository.EntrepotRepository;
import com.trans.asgard.domain.historiquevente.dto.HistoriqueVenteDto;
import com.trans.asgard.domain.historiquevente.mapper.HistoriqueVenteMapper;
import com.trans.asgard.domain.historiquevente.model.HistoriqueVente;
import com.trans.asgard.domain.historiquevente.repository.HistoriqueVenteRepository;
import com.trans.asgard.domain.historiquevente.service.interfaces.HistoriqueVenteService;
import com.trans.asgard.domain.product.model.Product;
import com.trans.asgard.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class HistoriqueVenteServiceImp implements HistoriqueVenteService {

    private final HistoriqueVenteRepository historiqueVenteRepository;
    private final ProductRepository productRepository;
    private final EntrepotRepository entrepotRepository;
    private  final HistoriqueVenteMapper mapper;

    @Override
    public HistoriqueVenteDto create(HistoriqueVenteDto dto) {

        HistoriqueVente h= mapper.toEntity(dto);

        h.setProduct(
                productRepository.findById(dto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Produit non trouvé"))
        );
        h.setEntrepot(
                entrepotRepository.findById(dto.getEntrepotId())
                        .orElseThrow(() -> new RuntimeException("Entrepot non trouvé"))
        );

        if (h.getDateVente() != null) {
            h.setJourSemaine(h.getDateVente().getDayOfWeek().toString());
            h.setMois(h.getDateVente().getMonthValue());
            h.setAnnee(h.getDateVente().getYear());
        }


        HistoriqueVente saved = historiqueVenteRepository.save(h);
        return mapper.toDto(saved);

    }

    @Override
    public HistoriqueVenteDto getById(Long id) {
        HistoriqueVente h = historiqueVenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HistoriqueVente non trouvé"));
        return mapper.toDto(h);
    }


    @Override
    public List<HistoriqueVenteDto> getAll() {
        return historiqueVenteRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<HistoriqueVenteDto> getByProductId(Long productId)
    {

        return historiqueVenteRepository.findByProductId(productId)
                .stream()
                .map(mapper::toDto)
                .toList();

    }


    @Override
    public List<HistoriqueVenteDto> getByEntrepotId(Long entrepotId)
    {

        return historiqueVenteRepository.findByEntrepotId(entrepotId)
                .stream()
                .map(mapper::toDto)
                .toList();

    }



    public  void delete(Long id)
    {
        HistoriqueVente h =historiqueVenteRepository.findById(id)
                .orElseThrow((()-> new RuntimeException("HistoriqueVente not found with id " + id)));

        historiqueVenteRepository.delete(h);
    }







}
