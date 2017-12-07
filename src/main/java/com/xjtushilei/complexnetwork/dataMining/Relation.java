package com.xjtushilei.complexnetwork.dataMining;

public class Relation {
    private String name1;
    private String id1;
    private String name2;
    private String id2;
    private String relationship;

    public Relation() {
        super();
    }

    public Relation(String name1, String id1, String name2, String id2, String relationship) {
        super();
        this.name1 = name1;
        this.id1 = id1;
        this.name2 = name2;
        this.id2 = id2;
        this.relationship = relationship;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Relation other = (Relation) obj;
        if (id1 == null) {
            if (other.id1 != null) {
                return false;
            }
        }
        if (id2 == null) {
            if (other.id2 != null) {
                return false;
            }
        }
        if (relationship == null) {
            if (other.relationship != null) {
                return false;
            }
        } else if (!relationship.equals(other.relationship)) {
            return false;
        }
        if ((id1.equals(other.id1) && id2.equals(other.id2)) || (id1.equals(other.id2) && id2.equals(other.id1))) {
            return true;
        } else {
            return false;
        }
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getId1() {
        return id1;
    }

    public void setId1(String id1) {
        this.id1 = id1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

}
