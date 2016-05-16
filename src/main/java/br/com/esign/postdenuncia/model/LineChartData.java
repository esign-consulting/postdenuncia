package br.com.esign.postdenuncia.model;

import java.util.List;
import java.util.Set;

public class LineChartData {

    private Set<QualidadeAr> qualidadesAr;
    private List<Medicao> ultimasMedicoes;

    public Set<QualidadeAr> getQualidadesAr() {
        return qualidadesAr;
    }

    public void setQualidadesAr(Set<QualidadeAr> qualidadesAr) {
        this.qualidadesAr = qualidadesAr;
    }

    public List<Medicao> getUltimasMedicoes() {
        return ultimasMedicoes;
    }

    public void setUltimasMedicoes(List<Medicao> ultimasMedicoes) {
        this.ultimasMedicoes = ultimasMedicoes;
    }

}