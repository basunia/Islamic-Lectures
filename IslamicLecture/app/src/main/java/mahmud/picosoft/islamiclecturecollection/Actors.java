package mahmud.picosoft.islamiclecturecollection;

public class Actors {
	private String name;
	private String link;
	private String contributor;
	private String time;

	public Actors() {

	}

	public Actors(String name, String link, String contributor, String time) {
		super();
		this.name = name;
		this.link = link;
		this.contributor = contributor;
		this.time = time;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getContributor() {
		return contributor;
	}

	public void setContributor(String contributor) {
		this.contributor = contributor;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
