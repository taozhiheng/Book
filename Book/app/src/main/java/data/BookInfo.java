package data;

/**
 * old version book data class
 * */

public class BookInfo {
	private String name,publisher,catolog;
	private String author;
    private int chapcount;
    private int color;

	public void setname(String name){
		this.name=name;
	}
	
	public String getname(){
		return this.name;
	}
	
	public void setauthor(String author){
		this.author=author;
	}
	public String getauthor(){
				return this.author;
	}
	
	public void setpublisher(String publisher){
		this.publisher=publisher;
	}
	
	public String getpublisher(){
		return this.publisher;
	}
	
	public void setcatolog(String catolog){
		this.catolog=catolog;
	} 
	
	public String getcatolog(){
		return this.catolog;
	}

    public void setChapcount(int count){
        this.chapcount=count;
    }

    public int getChapcount(){
        return this.chapcount;
    }

    public void setcolor(int color){
        this.color=color;
    }

    public int getColor(){
        return this.color;
    }

}
