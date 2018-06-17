package com.seanmcapp

import com.seanmcapp.repository._
import com.seanmcapp.repository.postgre.{CustomerInfo, DBComponent, PhotoInfo, VoteInfo}
import slick.lifted.TableQuery

import scala.concurrent.Future

object DBGenerator extends DBComponent {

  val photoData = Seq(
    Photo("1734075033692644433", "https://scontent-iad3-1.cdninstagram.com/vp/7c657bd346591a294cf51bd022285230/5B3FADF9/t51.2885-15/s640x640/sh0.08/e35/c0.96.764.764/28764036_154095071930810_8807155097025904640_n.jpg", 1520937874, "Lovita Soraya. FIB'13", "ui.cantik"),
    Photo("1733941761435551783", "https://scontent-iad3-1.cdninstagram.com/vp/c4cf66421a919c3a4f6ffe4f177043aa/5B3770EB/t51.2885-15/s640x640/sh0.08/e35/c0.90.719.719/28763493_1825816810826559_4422619036911665152_n.jpg", 1520921987, "Alexandra Geradina. Hukum 2015", "ugmcantik"),
    Photo("1732922049880722677", "https://scontent-iad3-1.cdninstagram.com/vp/dc43762dc957b6afd07155d482c22c14/5B3558EF/t51.2885-15/s640x640/sh0.08/e35/c0.82.720.720/28765965_152766868732757_2978863877690753024_n.jpg", 1520800428, "Maharani Augustina. Psikologi 2017", "ugmcantik"),
    Photo("1731935203634273121", "https://scontent-iad3-1.cdninstagram.com/vp/411ba88ea22054a6065e528fb94f46eb/5B28D813/t51.2885-15/e35/c0.0.639.639/28430270_333746737032702_6056355301307711488_n.jpg", 1520682786, "Shofiyah Fatin. Farmasi'15", "ui.cantik"),
    Photo("1731097470019890990", "https://scontent-iad3-1.cdninstagram.com/vp/82d754cc28b4b25ff09215691c71a47e/5B2C3D6A/t51.2885-15/e35/28753075_354612231692252_1219596637039493120_n.jpg", 1520582921, "Putri Astri. FIB'16", "ui.cantik"),
    Photo("1730438824258976379", "https://scontent-iad3-1.cdninstagram.com/vp/86249e207ea82438e36e276f832b7e82/5B2AC576/t51.2885-15/s640x640/sh0.08/e35/c0.83.720.720/28754110_410117279435304_7225939840208994304_n.jpg", 1520504404, "Dinda Ayu Saraswati. HI 2015", "undip.cantik"),
    Photo("1729782631481463370", "https://instagram.fbkk5-6.fna.fbcdn.net/vp/0ba194dfc7d52669298c113e1c3b3f4a/5B42EA15/t51.2885-15/s640x640/sh0.08/e35/c0.80.720.720/28158288_176736556448418_3791349330770657280_n.jpg", 1520426180, "Herdarudewi Prabandari. Arsitektur 2016", "ugmcantik"),
    Photo("1728847932219134760", "https://instagram.fbkk5-1.fna.fbcdn.net/vp/0cd8556bd52c9769b097bd22f0b21d61/5B2DA373/t51.2885-15/e35/28430295_196786274423409_8317235637929377792_n.jpg", 1520314755, "Anggis Dinda. FHUI'13", "ui.cantik"),
    Photo("1728246693352329796", "https://instagram.fbkk5-6.fna.fbcdn.net/vp/6e9e80e5149b1cc7a74dc0b9687e44ee/5B315757/t51.2885-15/s640x640/sh0.08/e35/c0.39.683.683/28156308_2035444966676825_3950744165649743872_n.jpg", 1520243082, "Aglenda Sherina. FEB 2017", "undip.cantik"),
    Photo("1728243660251000573", "https://instagram.fbkk5-6.fna.fbcdn.net/vp/2fceb8c6997be4ddf5fdc675b87c9934/5B4AEFDD/t51.2885-15/s640x640/sh0.08/e35/c0.84.720.720/28434304_1926520424329657_676162203262386176_n.jpg", 1520242720, "Indah. Hukum 2016", "ugmcantik"),
    Photo("1726876350122805178", "https://instagram.fbkk5-1.fna.fbcdn.net/vp/e106e6b604ca1ef0496be41c0b869e0f/5B41F2F5/t51.2885-15/e35/28155771_168482277206751_7241013586280054784_n.jpg", 1520079724, "Aloysia Agnes. FISIP'14", "ui.cantik"),
    Photo("1726791825592867475", "https://instagram.fbkk5-6.fna.fbcdn.net/vp/38f943b16e6c940cce1939d975cf93ad/5B4CBD55/t51.2885-15/s640x640/sh0.08/e35/c106.0.739.739/28435698_150901842256451_1964666281986621440_n.jpg", 1520069648, "Jihan Nabila. PSKG 2017", "undip.cantik"),
    Photo("1726769862867801699", "https://instagram.fbkk5-6.fna.fbcdn.net/vp/3433aaaa46aa764b10d8baff902888e0/5B435100/t51.2885-15/s640x640/sh0.08/e35/c1.0.1077.1077/28432640_168139057326628_7692042717806723072_n.jpg", 1520067030, "Nathasya Kristianti. Akuntansi 2017", "ugmcantik"),
    Photo("1725997696650836864", "https://instagram.fbkk5-6.fna.fbcdn.net/vp/a8aa3b9620030f078895142804bb4d20/5B42F657/t51.2885-15/s640x640/sh0.08/e35/28429260_337634723412141_4567374613201289216_n.jpg", 1519974980, "Siska Yulinar. Fisip 2017", "undip.cantik"),
    Photo("1725600127451021015", "https://instagram.fbkk5-1.fna.fbcdn.net/vp/57995b5de7aee97c88f7406c52fb272b/5B3AFA27/t51.2885-15/e35/28427083_1874894769218382_6716257067625086976_n.jpg", 1519927586, "Alifah Rahma Sari. FIB'12", "ui.cantik"),
    Photo("1725448487280549775", "https://instagram.fbkk5-6.fna.fbcdn.net/vp/2fb652e9fc1dcd4b20e8141330a2468c/5B498FD8/t51.2885-15/s640x640/sh0.08/e35/28430046_182611819134774_759290663916273664_n.jpg", 1519909510, "Andita Widia. Hukum 2015", "ugmcantik"),
    Photo("1724605304585652217", "https://instagram.fbkk5-6.fna.fbcdn.net/vp/ad4fa75295352226300606fbe7651712/5B46C1BC/t51.2885-15/s640x640/sh0.08/e35/c0.55.720.720/28154677_213544892531707_3522168358642384896_n.jpg", 1519808994, "Aisha Mutiara. FH 2013", "ugmcantik"),
    Photo("1724603753438604367", "https://instagram.fbkk5-6.fna.fbcdn.net/vp/6e6d2ff2cf606bc1e6759e5bd357e3d3/5B47E8F4/t51.2885-15/s640x640/sh0.08/e35/28152223_382092835595457_490940201024094208_n.jpg", 1519808809, "Dyah Ayu Sekar. FK 2017", "undip.cantik"),
    Photo("1724427908142256065", "https://instagram.fbkk5-1.fna.fbcdn.net/vp/a698f36d62c3c6d3a8190e5721690853/5B2D5B1F/t51.2885-15/s640x640/sh0.08/e35/c0.100.800.800/28153512_1744844548908049_168570839233462272_n.jpg", 1519787847, "Disa Putri Sabila. FIB'12", "ui.cantik"),
    Photo("1723813124132618409", "https://instagram.fbkk5-1.fna.fbcdn.net/vp/978493402d0732f37866e9a433e74217/5B4DF58D/t51.2885-15/e35/28153157_1834324190199736_81459775665078272_n.jpg", 1519714559, "Syifa Hanif. FKUI'12", "ui.cantik"),
  )

  val customerData = Seq(
    Customer(-209240150, "OMOM", true, 712),
    Customer(98387528, "Krisna Dibyo", true, 640),
    Customer(143635997, "Muhammad Redho Ayassa ", false, 326),
    Customer(146316672, "Arif Harsa", false, 278),
    Customer(88836419, "Faiz Hafidzuddin", false, 245),
    Customer(-111546505, "fuckin' deadline", true, 244),
    Customer(199902499, "Cahaya Ikhwan Putra", false, 239),
    Customer(186373768, "Rahmat Rasyidi Hakim", false, 236),
    Customer(274852283, "Bayu Suryadana", true, 226),
    Customer(203988626, "Muhammad Arrasy Rahman", false, 156)
  )

  def generate: Future[Unit] = {

    val photo = TableQuery[PhotoInfo]
    val customer = TableQuery[CustomerInfo]
    val vote = TableQuery[VoteInfo]

    import config.profile.api._
    val setup = DBIO.seq(
      (photo.schema ++ customer.schema ++ vote.schema).create,
      photo ++= photoData,
      customer ++= customerData
    )

    run(setup)
  }

}
