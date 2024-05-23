package ua.bank.moneyguard.services;

import org.springframework.transaction.annotation.Transactional;
import ua.bank.moneyguard.models.Client;

import java.util.List;

public interface ServiceExample<T> {
    @Transactional(readOnly = true)
    public T findById(Long id);
    @Transactional
    public Client save(T t);
    @Transactional
    public void saveAll(List<T> list);
    @Transactional
    public void deleteById(Long id);
    @Transactional
    public void update(Long id, T t);
}
