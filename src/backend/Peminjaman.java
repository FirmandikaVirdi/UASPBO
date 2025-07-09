/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend;

import java.util.ArrayList;
import java.sql.*;

public class Peminjaman {

    private int idpeminjaman;
    private Anggota anggota;
    private Buku buku;
    private String tanggalpinjam;
    private String tanggalkembali;
    private String status;

    public Peminjaman() {
    }

    public Peminjaman(Anggota anggota, Buku buku, String tanggalpinjam, String tanggalkembali) {
        this.anggota = anggota;
        this.buku = buku;
        this.tanggalpinjam = tanggalpinjam;
        this.tanggalkembali = tanggalkembali;
    }

    public int getIdPeminjaman() {
        return idpeminjaman;
    }

    public void setIdPeminjaman(int idpeminjaman) {
        this.idpeminjaman = idpeminjaman;
    }

    public Anggota getAnggota() {
        return anggota;
    }

    public void setAnggota(Anggota anggota) {
        this.anggota = anggota;
    }

    public Buku getBuku() {
        return buku;
    }

    public void setBuku(Buku buku) {
        this.buku = buku;
    }

    public String getTanggalpinjam() {
        return tanggalpinjam;
    }

    public void setTanggalpinjam(String tanggalpinjam) {
        this.tanggalpinjam = tanggalpinjam;
    }

    public String getTanggalkembali() {
        return tanggalkembali;
    }

    public void setTanggalkembali(String tanggalkembali) {
        this.tanggalkembali = tanggalkembali;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Peminjaman getById(int id) {
        Peminjaman pem = new Peminjaman();
        String sql = "SELECT * FROM peminjaman "
                + "LEFT JOIN anggota ON peminjaman.idanggota = anggota.idanggota "
                + "LEFT JOIN buku ON peminjaman.idbuku = buku.idbuku "
                + "LEFT JOIN kategori ON buku.idkategori = kategori.idkategori "
                + "WHERE idpeminjaman = " + id;

        ResultSet rs = DBHelper.selectQuery(sql);
        try {
            while (rs.next()) {
                pem = new Peminjaman();
                pem.setIdPeminjaman(rs.getInt("idpeminjaman"));
                pem.setTanggalpinjam(rs.getString("tanggalpinjam"));
                pem.setTanggalkembali(rs.getString("tanggalkembali"));
                pem.setStatus(rs.getString("status"));

                Anggota ang = new Anggota();
                ang.setIdAnggota(rs.getInt("idanggota"));
                ang.setNama(rs.getString("nama"));
                ang.setAlamat(rs.getString("alamat"));
                ang.setTelepon(rs.getString("telepon"));

                Kategori kat = new Kategori();
                kat.setIdkategori(rs.getInt("idkategori"));
                kat.setNama(rs.getString("nama"));
                kat.setKeterangan(rs.getString("keterangan"));

                Buku buku = new Buku();
                buku.setIdBuku(rs.getInt("idbuku"));
                buku.setKategori(kat);
                buku.setJudul(rs.getString("judul"));
                buku.setPenerbit(rs.getString("penerbit"));
                buku.setPenulis(rs.getString("penulis"));

                pem.setAnggota(ang);
                pem.setBuku(buku);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pem;
    }

    public ArrayList<Peminjaman> getAll() {
        ArrayList<Peminjaman> list = new ArrayList();
        ResultSet rs = DBHelper.selectQuery("SELECT * FROM peminjaman "
                + "LEFT JOIN anggota ON peminjaman.idanggota = anggota.idanggota "
                + "LEFT JOIN buku ON peminjaman.idbuku = buku.idbuku "
                + "LEFT JOIN kategori ON buku.idkategori = kategori.idkategori");

        try {
            while (rs.next()) {
                Peminjaman pem = new Peminjaman();
                pem.setIdPeminjaman(rs.getInt("idpeminjaman"));
                pem.setTanggalpinjam(rs.getString("tanggalpinjam"));
                pem.setTanggalkembali(rs.getString("tanggalkembali"));
                pem.setStatus(rs.getString("status"));

                Anggota ang = new Anggota(rs.getString("nama"), rs.getString("alamat"), rs.getString("telepon"));
                ang.setIdAnggota(rs.getInt("idanggota"));

                Kategori kat = new Kategori(rs.getString("nama"), rs.getString("keterangan"));
                kat.setIdkategori(rs.getInt("idkategori"));

                Buku buku = new Buku(kat, rs.getString("judul"), rs.getString("penerbit"), rs.getString("penulis"));
                buku.setIdBuku(rs.getInt("idbuku"));

                pem.setAnggota(ang);
                pem.setBuku(buku);

                list.add(pem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void save() {
        if (getById(idpeminjaman).getIdPeminjaman() == 0) {
            String SQL = "INSERT INTO peminjaman (idanggota, idbuku, tanggalpinjam, tanggalkembali, status) VALUES("
                    + "'" + this.getAnggota().getIdAnggota() + "', "
                    + "'" + this.getBuku().getIdBuku() + "', "
                    + "'" + this.tanggalpinjam + "', "
                    + (this.tanggalkembali == null ? "NULL" : "'" + this.tanggalkembali + "'") + ", "
                    + "'" + this.status + "'"
                    + ")";

            this.idpeminjaman = DBHelper.insertQueryGetId(SQL);
        } else {
            String SQL = "UPDATE peminjaman SET "
                    + "idanggota = '" + this.getAnggota().getIdAnggota() + "', "
                    + "idbuku = '" + this.getBuku().getIdBuku() + "', "
                    + "tanggalpinjam = '" + this.tanggalpinjam + "', "
                    + "tanggalkembali = " + (this.tanggalkembali == null ? "NULL" : "'" + this.tanggalkembali + "'") + ", "
                    + "status = '" + this.status + "' "
                    + "WHERE idpeminjaman = " + this.idpeminjaman;

            DBHelper.executeQuery(SQL);
        }
    }

    public ArrayList<Peminjaman> search(String keyword) {
        ArrayList<Peminjaman> ListPeminjaman = new ArrayList<>();

        // Mapping untuk status keyword
        String statusKeyword = keyword.toLowerCase().trim();
        if (statusKeyword.equals("dipinjam")) {
            statusKeyword = "0";
        } else if (statusKeyword.equals("dikembalikan")) {
            statusKeyword = "1";
        }

        // Buat query dengan pengecekan status menggunakan EQUALS, bukan LIKE
        String sql = "SELECT * FROM peminjaman "
                + "LEFT JOIN anggota ON peminjaman.idanggota = anggota.idanggota "
                + "LEFT JOIN buku ON peminjaman.idbuku = buku.idbuku "
                + "WHERE anggota.nama LIKE '%" + keyword + "%' "
                + "OR buku.judul LIKE '%" + keyword + "%' "
                + "OR CAST(peminjaman.idpeminjaman AS CHAR) LIKE '%" + keyword + "%' ";

        // Deteksi jika keyword = 0 atau 1 → tambahkan sebagai filter status PERSIS
        if (keyword.equals("0") || keyword.equals("1")
                || keyword.equalsIgnoreCase("dipinjam") || keyword.equalsIgnoreCase("dikembalikan")) {
            sql += "OR peminjaman.status = '" + statusKeyword + "'";
        }

        ResultSet rs = DBHelper.selectQuery(sql);
        try {
            while (rs.next()) {
                Peminjaman pem = new Peminjaman();
                pem.setIdPeminjaman(rs.getInt("idpeminjaman"));
                pem.setTanggalpinjam(rs.getString("tanggalpinjam"));
                pem.setTanggalkembali(rs.getString("tanggalkembali"));
                pem.setStatus(rs.getString("status"));

                Anggota ang = new Anggota();
                ang.setIdAnggota(rs.getInt("idanggota"));
                ang.setNama(rs.getString("nama"));
                ang.setAlamat(rs.getString("alamat"));
                ang.setTelepon(rs.getString("telepon"));

                Buku buk = new Buku();
                buk.setIdBuku(rs.getInt("idbuku"));
                buk.setJudul(rs.getString("judul"));
                buk.setPenerbit(rs.getString("penerbit"));

                pem.setAnggota(ang);
                pem.setBuku(buk);

                ListPeminjaman.add(pem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ListPeminjaman;
    }

    public void delete() {
        String SQL = "DELETE FROM peminjaman WHERE idpeminjaman = " + this.idpeminjaman;
        DBHelper.executeQuery(SQL);
    }
}
