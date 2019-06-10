-- phpMyAdmin SQL Dump
-- version 4.8.0.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 07, 2019 at 10:46 AM
-- Server version: 10.1.32-MariaDB
-- PHP Version: 7.2.5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `bayesian`
--

-- --------------------------------------------------------

--
-- Table structure for table `cart`
--

CREATE TABLE `cart` (
  `cart_id` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `qty` int(11) NOT NULL,
  `total` double NOT NULL,
  `cart_checkout` varchar(30) NOT NULL,
  `status` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `credit_card`
--

CREATE TABLE `credit_card` (
  `card_id` int(11) NOT NULL,
  `card_type` varchar(30) NOT NULL,
  `card_number` int(11) NOT NULL,
  `card_cvv` int(11) NOT NULL,
  `card_month` varchar(10) NOT NULL,
  `card_year` varchar(10) NOT NULL,
  `card_name` varchar(30) NOT NULL,
  `card_user` varchar(50) NOT NULL,
  `verified` varchar(10) NOT NULL,
  `status` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `dataset_payment`
--

CREATE TABLE `dataset_payment` (
  `id_dataset_payment` int(11) NOT NULL,
  `pay_1` int(11) NOT NULL,
  `pay_2` int(11) NOT NULL,
  `pay_3` int(11) NOT NULL,
  `pay_4` int(11) NOT NULL,
  `pay_5` int(11) NOT NULL,
  `pay_6` int(11) NOT NULL,
  `bill_amt1` int(11) NOT NULL,
  `bill_amt2` int(11) NOT NULL,
  `bill_amt3` int(11) NOT NULL,
  `bill_amt4` int(11) NOT NULL,
  `bill_amt5` int(11) NOT NULL,
  `bill_amt6` int(11) NOT NULL,
  `pay_amt1` int(11) NOT NULL,
  `pay_amt2` int(11) NOT NULL,
  `pay_amt3` int(11) NOT NULL,
  `pay_amt4` int(11) NOT NULL,
  `pay_amt5` int(11) NOT NULL,
  `pay_amt6` int(11) NOT NULL,
  `status_payment` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `ip`
--

CREATE TABLE `ip` (
  `ip_id` int(11) NOT NULL,
  `ip_username` varchar(50) NOT NULL,
  `ip_transaction` varchar(50) NOT NULL,
  `ip_payment` varchar(50) NOT NULL,
  `ip_report` varchar(50) NOT NULL,
  `status` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `payment`
--

CREATE TABLE `payment` (
  `payment_id` int(11) NOT NULL,
  `payment_amount` double NOT NULL,
  `payment_card` varchar(50) NOT NULL,
  `payment_type` int(11) NOT NULL,
  `payment_period` int(11) NOT NULL,
  `payment_status` varchar(50) NOT NULL,
  `status` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
  `product_id` int(11) NOT NULL,
  `product_name` varchar(100) NOT NULL,
  `product_image` varchar(100) NOT NULL,
  `product_stock` int(11) NOT NULL,
  `product_description` text NOT NULL,
  `product_price` float NOT NULL,
  `status` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `report`
--

CREATE TABLE `report` (
  `report_id` int(11) NOT NULL,
  `report_user` int(11) NOT NULL,
  `report_product` int(11) NOT NULL,
  `report_payment` int(11) NOT NULL,
  `report_transaction` int(11) NOT NULL,
  `report_creditcard` int(11) NOT NULL,
  `report_message` int(11) NOT NULL,
  `status` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `transaction`
--

CREATE TABLE `transaction` (
  `transaction_id` int(11) NOT NULL,
  `card_id` int(11) NOT NULL,
  `transaction_amount` double NOT NULL,
  `transaction_card` varchar(30) NOT NULL,
  `transaction_payment` int(11) NOT NULL,
  `transaction_proses` varchar(30) NOT NULL,
  `ip_transaction` varchar(20) NOT NULL,
  `status` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id_user` int(100) NOT NULL,
  `username` varchar(100) NOT NULL,
  `password` varchar(50) NOT NULL,
  `nama` varchar(50) NOT NULL,
  `jenis_kelamin` varchar(20) NOT NULL,
  `email` varchar(50) NOT NULL,
  `no_telp` varchar(25) NOT NULL,
  `transaction_limit` int(11) NOT NULL,
  `image` varchar(100) NOT NULL,
  `tres` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `cart`
--
ALTER TABLE `cart`
  ADD PRIMARY KEY (`cart_id`),
  ADD UNIQUE KEY `cart_id` (`cart_id`);

--
-- Indexes for table `credit_card`
--
ALTER TABLE `credit_card`
  ADD PRIMARY KEY (`card_id`),
  ADD UNIQUE KEY `card_id` (`card_id`);

--
-- Indexes for table `dataset_payment`
--
ALTER TABLE `dataset_payment`
  ADD PRIMARY KEY (`id_dataset_payment`),
  ADD UNIQUE KEY `id_dataset_payment` (`id_dataset_payment`);

--
-- Indexes for table `ip`
--
ALTER TABLE `ip`
  ADD PRIMARY KEY (`ip_id`),
  ADD UNIQUE KEY `ip_id` (`ip_id`);

--
-- Indexes for table `payment`
--
ALTER TABLE `payment`
  ADD PRIMARY KEY (`payment_id`),
  ADD UNIQUE KEY `payment_id` (`payment_id`);

--
-- Indexes for table `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`product_id`),
  ADD UNIQUE KEY `product_id` (`product_id`);

--
-- Indexes for table `report`
--
ALTER TABLE `report`
  ADD PRIMARY KEY (`report_id`),
  ADD UNIQUE KEY `report_id` (`report_id`);

--
-- Indexes for table `transaction`
--
ALTER TABLE `transaction`
  ADD PRIMARY KEY (`transaction_id`),
  ADD UNIQUE KEY `transaction_id` (`transaction_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `id_user` (`id_user`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
