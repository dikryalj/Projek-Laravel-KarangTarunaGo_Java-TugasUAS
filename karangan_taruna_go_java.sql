-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 17 Agu 2025 pada 07.40
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `karangan_taruna_go_java`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `users`
--
-- Tabel 'users' harus dibuat terlebih dahulu karena tabel lain merujuk padanya.
--
CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `email` varchar(100) NOT NULL,
  `username` varchar(50) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('admin','user') DEFAULT 'user',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `active` tinyint(1) DEFAULT 1,
  `status` varchar(20) DEFAULT 'active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `anggota`
--
-- Tabel 'anggota' merujuk ke tabel 'users' melalui kolom 'username'
--
CREATE TABLE `anggota` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `nama_lengkap` varchar(100) NOT NULL,
  `gender` enum('Laki-Laki','Perempuan') NOT NULL,
  `jabatan` enum('Ketua','Wakil Ketua','Seketaris','Bendahara') NOT NULL,
  `status` varchar(20) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `dibuat` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `kategori`
--

CREATE TABLE `kategori` (
  `id` int(11) NOT NULL,
  `nama_kategori` varchar(255) NOT NULL,
  `jenis_transaksi` varchar(50) NOT NULL,
  `status` varchar(20) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `agenda`
--
-- Menambahkan FOREIGN KEY ke tabel 'users'
--
CREATE TABLE `agenda` (
  `id_agenda` int(11) NOT NULL,
  `tanggal` date NOT NULL,
  `waktu_mulai` time NOT NULL,
  `waktu_selesai` time NOT NULL,
  `nama_agenda` varchar(255) NOT NULL,
  `deskripsi` text DEFAULT NULL,
  `dibuat_oleh_id` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `pencatatan`
--
-- Menambahkan FOREIGN KEY ke tabel 'anggota' dan 'kategori'
--
CREATE TABLE `pencatatan` (
  `id` int(11) NOT NULL,
  `jenis_transaksi` varchar(255) NOT NULL,
  `nominal` double NOT NULL,
  `id_anggota` int(11) DEFAULT NULL,
  `deskripsi` text DEFAULT NULL,
  `id_kategori` int(11) DEFAULT NULL,
  `dibuat_oleh` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `users`
--

INSERT INTO `users` (`id`, `email`, `username`, `nama`, `password`, `role`, `created_at`, `active`, `status`) VALUES
(1, 'admin@gmail.com', 'admin', 'Admin Utama', '12345', 'admin', '2025-08-15 10:02:52', 1, 'active'),
(3, 'dikry@gmail.com', 'dikry@gmail.com', 'dikry', '$2a$10$36YkQNGvOdnb9syQj0944Ov8GBBk3T/y1QilsDJ/Y0MpnYWF7R1je', 'admin', '2025-08-16 21:28:10', 1, 'active'),
(4, 'dzikry@gmail.com', 'dzikry@gmail.com', 'dzikry', '$2a$10$jGyYUlN05zbwbuL08sU46.4KQJbPVwa0.ak.JZVT4qaODgWYLUEuG', 'admin', '2025-08-17 00:41:30', 1, 'active'),
(5, 'luki@gmail.com', 'luki@gmail.com', 'luki suka melon', '$2a$10$cGqMkHQEen6.wYB..dqp/OC8ddgppp0WvXGNIUgx6msyBZ8QtDqHO', 'admin', '2025-08-17 04:38:56', 1, 'active');

--
-- Dumping data untuk tabel `anggota`
--

INSERT INTO `anggota` (`id`, `username`, `nama_lengkap`, `gender`, `jabatan`, `status`, `created_at`, `updated_at`, `dibuat`) VALUES
(2, 'dikry@gmail.com', 'dikry', 'Laki-Laki', 'Ketua', 'Aktif', '2025-08-16 21:28:10', '2025-08-17 01:04:50', '2025-08-16 21:28:10'),
(17, 'dzikry@gmail.com', 'dzikry', 'Laki-Laki', 'Ketua', 'Aktif', '2025-08-17 00:41:30', '2025-08-17 01:04:50', '2025-08-17 00:41:30'),
(18, 'luki@gmail.com', 'luki suka melon', 'Laki-Laki', 'Wakil Ketua', 'Aktiv', '2025-08-17 04:38:56', '2025-08-17 04:38:56', '2025-08-17 04:38:56');

--
-- Dumping data untuk tabel `kategori`
--

INSERT INTO `kategori` (`id`, `nama_kategori`, `jenis_transaksi`, `status`, `created_at`) VALUES
(1, 'dd', 'Pemasukan', 'Aktif', '2025-08-16 19:33:56'),
(2, 'Gaji', 'Pemasukan', 'Aktif', '2025-08-16 20:03:24'),
(3, 'Donasi', 'Pemasukan', 'Aktif', '2025-08-16 20:03:24'),
(4, 'Bantuan Sosial', 'Pemasukan', 'Aktif', '2025-08-16 20:03:24'),
(5, 'Biaya Operasional', 'Pengeluaran', 'Aktif', '2025-08-16 20:03:24'),
(6, 'Sewa Gedung', 'Pengeluaran', 'Aktif', '2025-08-16 20:03:24'),
(7, 'Konsumsi Acara', 'Pengeluaran', 'Aktif', '2025-08-16 20:03:24'),
(8, 'dana', 'Pengeluaran', 'Aktif', '2025-08-16 20:13:17');

--
-- Dumping data untuk tabel `pencatatan`
--

-- Tidak ada data yang dibuang untuk tabel `pencatatan`

--
-- Dumping data untuk tabel `agenda`
--

-- Tidak ada data yang dibuang untuk tabel `agenda`


--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indeks untuk tabel `anggota`
--
ALTER TABLE `anggota`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD KEY `idx_username` (`username`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_created_at` (`created_at`);

--
-- Indeks untuk tabel `kategori`
--
ALTER TABLE `kategori`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nama_kategori` (`nama_kategori`);

--
-- Indeks untuk tabel `agenda`
--
ALTER TABLE `agenda`
  ADD PRIMARY KEY (`id_agenda`),
  ADD KEY `fk_agenda_user` (`dibuat_oleh_id`);

--
-- Indeks untuk tabel `pencatatan`
--
ALTER TABLE `pencatatan`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_pencatatan_kategori` (`id_kategori`),
  ADD KEY `fk_pencatatan_anggota` (`id_anggota`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT untuk tabel `anggota`
--
ALTER TABLE `anggota`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT untuk tabel `kategori`
--
ALTER TABLE `kategori`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT untuk tabel `agenda`
--
ALTER TABLE `agenda`
  MODIFY `id_agenda` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `pencatatan`
--
ALTER TABLE `pencatatan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Batasan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Batasan untuk tabel `agenda`
--
-- Menghubungkan 'dibuat_oleh_id' di tabel `agenda` dengan 'id' di tabel `users`
--
ALTER TABLE `agenda`
  ADD CONSTRAINT `fk_agenda_user` FOREIGN KEY (`dibuat_oleh_id`) REFERENCES `users` (`id`);

--
-- Batasan untuk tabel `pencatatan`
--
-- Menghubungkan 'id_anggota' di tabel `pencatatan` dengan 'id' di tabel `anggota`
--
ALTER TABLE `pencatatan`
  ADD CONSTRAINT `fk_pencatatan_anggota` FOREIGN KEY (`id_anggota`) REFERENCES `anggota` (`id`);
--
-- Menghubungkan 'id_kategori' di tabel `pencatatan` dengan 'id' di tabel `kategori`
--
ALTER TABLE `pencatatan`
  ADD CONSTRAINT `fk_pencatatan_kategori` FOREIGN KEY (`id_kategori`) REFERENCES `kategori` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
