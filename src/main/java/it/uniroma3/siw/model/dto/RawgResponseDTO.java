package it.uniroma3.siw.model.dto;

import java.util.List;

public class RawgResponseDTO {
    
    private Integer count;
    private String next;
    private String previous;
    private List<RawgGameDTO> results;

    public RawgResponseDTO() {
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<RawgGameDTO> getResults() {
        return results;
    }

    public void setResults(List<RawgGameDTO> results) {
        this.results = results;
    }
}
