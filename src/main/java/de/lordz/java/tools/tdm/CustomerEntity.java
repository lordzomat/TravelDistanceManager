package de.lordz.java.tools.tdm;
import javax.persistence.*;  

@Entity
@Table(name="tbCustomers")
public class CustomerEntity {

	@Id
	@Column(name="coId")
	private Integer id;
	@Column(name="coName", nullable = false)
	private String name;
	@Column(name="coDistance", nullable = false)
	private Float distance;	

	public CustomerEntity(String name, float distance) {
		super();
		//this.id = id;
		this.name = name;
		this.distance = distance;
	}
	
	public CustomerEntity() {
		super();
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;		
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public float getDistance() {
		return this.distance;
	}
	
	public void setDistance(float distance) {
		this.distance = distance;		
	}
}
