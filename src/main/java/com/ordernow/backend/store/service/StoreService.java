package com.ordernow.backend.store.service;

import com.ordernow.backend.common.dto.PageResponse;
import com.ordernow.backend.menu.service.MenuService;
import com.ordernow.backend.store.model.dto.StoreUpdateRequest;
import com.ordernow.backend.store.model.entity.Store;
import com.ordernow.backend.store.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class StoreService {
    private final StoreRepository storeRepository;
    private final MenuService menuService;
    private static final Set<String> ALLOWED_SORT_BY_FIELDS = Set.of("averageSpend", "rating", "name");
    private static final Set<String> ALLOWED_SORT_DIR_FIELDS = Set.of("asc", "desc");


    @Autowired
    public StoreService(StoreRepository storeRepository, MenuService menuService) {
        this.storeRepository = storeRepository;
        this.menuService = menuService;
    }

    public List<String> getStoresIdFilteredAndSorted(String keyword, String sortBy, String sortDir) throws InvalidParameterException {
        List<Store> stores;
        switch (sortBy) {
            case "name" -> stores = sortDir.equals("asc")
                    ? storeRepository.findByNameContainingOnlyIdOrderByNameAsc(keyword)
                    : storeRepository.findByNameContainingOnlyIdOrderByNameDesc(keyword);
            case "rating" -> stores = sortDir.equals("asc")
                    ? storeRepository.findByNameContainingOnlyIdOrderByRatingAsc(keyword)
                    : storeRepository.findByNameContainingOnlyIdOrderByRatingDesc(keyword);
            case "averageSpend" -> stores = sortDir.equals("asc")
                    ? storeRepository.findByNameContainingOnlyIdOrderByAverageSpendAsc(keyword)
                    : storeRepository.findByNameContainingOnlyIdOrderByAverageSpendDesc(keyword);
            default -> {
                return null;
            }
        }
        return stores.stream().map(Store::getId).toList();
    }

    public Store validStoreId(String storeId)
            throws NoSuchElementException {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new NoSuchElementException("Store not found"));
    }

    public List<Store> getStoreByIds(List<String> ids) {
        return ids.stream()
                .map(storeRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public String createAndSaveStore(String phoneNumber) {
        Store store = Store.createDefaultStore();
        String menuId = menuService.createAndSaveMenu();
        store.setMenuId(menuId);
        store.setPhoneNumber(phoneNumber);
        return storeRepository.save(store).getId();
    }

    public void updateStore(String storeId, StoreUpdateRequest request)
            throws NoSuchElementException {

        Store currentStore = validStoreId(storeId);
        currentStore.setName(request.getName());
        currentStore.setPicture(request.getPicture());
        currentStore.setAddress(request.getAddress());
        currentStore.setDescription(request.getDescription());
        currentStore.setBusinessHours(request.getBusinessHours());
        storeRepository.save(currentStore);
    }

    public boolean changeBusinessStatus(String storeId)
            throws NoSuchElementException {

        Store store = validStoreId(storeId);
        store.setIsBusiness(!store.getIsBusiness());
        storeRepository.save(store);
        return store.getIsBusiness();
    }

    private Sort createSort(String sortBy, String sortDir) {
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        return Sort.by(direction, sortBy);
    }

    public PageResponse<Store> searchStores(
            String keyword, String sortBy, String sortDir,
            int page, int size)
            throws IllegalArgumentException {

        if(page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid page number or page size");
        }
        if(!ALLOWED_SORT_BY_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sortBy value. Allowed values are 'averageSpend', 'rating' and 'name'.");
        }
        if(!ALLOWED_SORT_DIR_FIELDS.contains(sortDir)) {
            throw new IllegalArgumentException("Invalid sortDir value. Allowed values are 'asc' and 'desc'.");
        }

        Sort sort = createSort(sortBy, sortDir);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Store> storePage = storeRepository.findByNameContainingIgnoreCase(keyword, pageRequest);
        return PageResponse.of(storePage);
    }

    public void updateStoreByReview(String storeId, double rating, double averageSpend, int size) {
        Store store = validStoreId(storeId);
        store.setRating((store.getRating()*size + rating) / (size+1));
        store.setAverageSpend((store.getAverageSpend()*size + averageSpend) / (size+1));
        storeRepository.save(store);
    }
}
