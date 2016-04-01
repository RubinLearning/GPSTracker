package domain;

import javax.persistence.*;

@Entity
@Table(name="TRACK_IMG")
public class TrackIMG {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TRACK_ID")
    Track track;

    @Column(name = "IMG", length = 10000000)
    @Lob
    byte[] IMG;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public byte[] getIMG() {
        return IMG;
    }

    public void setIMG(byte[] IMG) {
        this.IMG = IMG;
    }
}
